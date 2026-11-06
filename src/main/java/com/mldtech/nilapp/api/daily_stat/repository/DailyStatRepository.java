package com.mldtech.nilapp.api.daily_stat.repository;

import com.mldtech.nilapp.api.daily_stat.model.DailyStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyStatRepository extends JpaRepository<DailyStat, Long> {

    List<DailyStat> findByUserId(Long userId);

    Optional<DailyStat> findByUserIdAndDate(Long userId, LocalDate date);
    /**
     * Retrieves an ordered historical collection of daily statistics for a user within a custom date boundary.
     *
     * @param userId    the unique tracking identifier of the target user
     * @param startDate the start date of the timeline range
     * @param endDate   the end date of the timeline range
     * @return a collection list of daily stats entities
     */
    List<DailyStat> findByUserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

    boolean existsByUserIdAndDate(Long userId, LocalDate date);

    @Query("""
    SELECT u.userId, u.username, SUM(d.totalDeviceSteps)
    FROM DailyStat d
    JOIN User u ON u.userId = d.userId
    WHERE d.date BETWEEN :start AND :end
    GROUP BY u.userId, u.username
    ORDER BY SUM(d.totalDeviceSteps) DESC
""")
    List<Object[]> getTopSteps(LocalDate start, LocalDate end);

    /**
     * Sums up all raw sensor activity steps logged directly from the phone device within a specific date range.
     *
     * @param userId the tracking identifier of the target user
     * @param start  the starting calendar date boundary
     * @param end    the closing calendar date boundary
     * @return cumulative physical sensor step count
     */
    @Query("SELECT COALESCE(SUM(ds.totalDeviceSteps), 0) FROM DailyStat ds WHERE ds.userId = :userId AND ds.date BETWEEN :start AND :end")
    long sumDeviceStepsByUserIdAndDateRange(@org.springframework.data.repository.query.Param("userId") Long userId, @org.springframework.data.repository.query.Param("start") java.time.LocalDate start, @org.springframework.data.repository.query.Param("end") java.time.LocalDate end);
    /**
     * Sums up all historical sensor steps logged directly from the phone device across all time.
     *
     * @param userId the tracking identifier of the target user
     * @return lifetime physical sensor step count
     */
    @Query("SELECT COALESCE(SUM(ds.totalDeviceSteps), 0) FROM DailyStat ds WHERE ds.userId = :userId")
    long sumLifetimeDeviceStepsByUserId(@org.springframework.data.repository.query.Param("userId") Long userId);

    // ======================================================================
    // 🔄 PASS 1: CONSUME ALLOCATED CONTRIBUTIONS INTO THE CORRESPONDING DATES
    // ======================================================================
    @Modifying
    @Transactional
    @Query(value = """
        INSERT INTO daily_stats (
            user_id, 
            date, 
            total_claimed_steps, 
            coins, 
            total_device_steps, 
            is_synced_to_lifetime
        )
        SELECT 
            c.user_id,
            CAST(c.created_at AS date) as entry_date,
            SUM(COALESCE(c.steps_contributed, 0)) as agg_claimed_steps,
            SUM(COALESCE(c.coins_contributed, 0)) as agg_coins,
            0 as total_device_steps,
            false as is_synced_to_lifetime
        FROM contributions c
        WHERE c.contribution_status_id = 5 
          AND c.is_synced_to_daily_stats = false
        GROUP BY c.user_id, CAST(c.created_at AS date)
        ON CONFLICT (user_id, date) 
        DO UPDATE SET 
            total_claimed_steps   = daily_stats.total_claimed_steps + EXCLUDED.total_claimed_steps,
            coins                 = daily_stats.coins + EXCLUDED.coins,
            is_synced_to_lifetime = false -- Signals to the streaming pass that new deltas are ready!
        """, nativeQuery = true)
    int aggregateAllocatedContributionsToDailyStats();

    // ======================================================================
    // 🔒 PASS 2: FLIP THE CONTRIBUTION FLAG SO THEY ARE NEVER RE-PROCESSED
    // ======================================================================
    @Modifying
    @Transactional
    @Query(value = """
        UPDATE contributions 
        SET is_synced_to_daily_stats = true 
        WHERE contribution_status_id = 5 
          AND is_synced_to_daily_stats = false
        """, nativeQuery = true)
    int clearProcessedContributionsQueue();

    // ======================================================================
    // 📊 PASS 3A: STREAM DATA DELTAS AND UPDATE USER PROFILE ACCOUNT FILES
    // ======================================================================
    @Modifying
    @Transactional
    @Query(value = """
        WITH calculated_deltas AS (
            SELECT 
                ds.user_id,
                (ds.total_device_steps - ds.lifetime_synced_device_steps) as delta_device,
                (ds.total_claimed_steps - ds.lifetime_synced_claimed_steps) as delta_claimed,
                (ds.coins - ds.lifetime_synced_coins) as delta_coins
            FROM daily_stats ds
            WHERE ds.is_synced_to_lifetime = false
        )
        UPDATE users u
        SET 
            total_lifetime_steps         = u.total_lifetime_steps + src.total_device_sum,
            total_lifetime_claimed_steps = u.total_lifetime_claimed_steps + src.total_claimed_sum,
            total_lifetime_coins         = u.total_lifetime_coins + src.total_coins_sum
        FROM (
            SELECT 
                user_id,
                SUM(delta_device) as total_device_sum,
                SUM(delta_claimed) as total_claimed_sum,
                SUM(delta_coins) as total_coins_sum
            FROM calculated_deltas
            GROUP BY user_id
        ) src
        WHERE u.user_id = src.user_id
        """, nativeQuery = true)
    int streamWorkoutDeltasToUserLifetimes();

    // ======================================================================
    // ✅ PASS 3B: SET DAILY SYNCHRONIZATION COLUMN PROGRESS TRACKERS
    // ======================================================================
    @Modifying
    @Transactional
    @Query(value = """
        UPDATE daily_stats ds
        SET 
            lifetime_synced_device_steps  = ds.total_device_steps,
            lifetime_synced_claimed_steps = ds.total_claimed_steps,
            lifetime_synced_coins         = ds.coins,
            is_synced_to_lifetime         = true
        WHERE ds.is_synced_to_lifetime = false
        """, nativeQuery = true)
    int clearProcessedDailyStatsQueue();


}


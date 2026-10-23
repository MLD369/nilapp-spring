package com.mldtech.nilapp.api.daily_stat.repository;

import com.mldtech.nilapp.api.daily_stat.model.DailyStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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



}


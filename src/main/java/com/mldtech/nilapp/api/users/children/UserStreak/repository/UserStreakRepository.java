package com.mldtech.nilapp.api.users.children.UserStreak.repository;

import com.mldtech.nilapp.api.users.children.UserStreak.model.UserStreak;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;


import java.time.LocalDate;

import java.util.List;
import java.util.Optional;

public interface UserStreakRepository extends JpaRepository<UserStreak, Long> {

    /**
     * Finds a user's streak profile by navigating through the embedded User relationship.
     * Maps cleanly to the user_id foreign key column in your database.
     *
     * @param userId The target user identification token
     * @return Optional wrapper containing the user streak entity profile
     */
    Optional<UserStreak> findByUser_UserId(Long userId);

    @Query("""
    SELECT u.userId, u.username, s.currentStreak
    FROM UserStreak s
    JOIN User u ON u.userId = s.user.userId
    ORDER BY s.currentStreak DESC
""")
    List<Object[]> getTopStreaks();

    // 1. Counts BOTH Pending (1) and Approved (2) claims to prevent accidental breaks
    /**
     * Counts BOTH Pending (1) and Approved (2) claims to prevent accidental breaks
     */
    @Query(value = """
        SELECT COUNT(contribution_id) 
        FROM contributions 
        WHERE user_id = :userId 
          AND contribution_status_id IN (1, 2)
          -- ESCAPED THE COLONS HERE TO FIX THE HIBERNATE SYNTAX ERROR:
          AND (created_at AT TIME ZONE 'America/New_York')\\:\\:date = CURRENT_DATE - INTERVAL '1 day'
        """, nativeQuery = true)
    long countYesterdayTotalClaimsAttempts(@Param("userId") Long userId);


    //  Option A: Consumes 1 shield. Bypasses the streak reset but does NOT increase streak count.
    // Option A: User chooses to spend 1 Shield (+ Increments your lifetime auditing metrics)
    @Modifying
    @Query(value = """
        UPDATE user_streaks 
        SET streak_shields = streak_shields - 1,
            total_shields_used = total_shields_used + 1,
            last_completed_date = CURRENT_DATE - INTERVAL '1 day',
            updated_at = NOW()
        WHERE user_id = :userId AND streak_shields > 0;
        """, nativeQuery = true)
    int consumeShieldWithShortAd(@Param("userId") Long userId);

    /**
     * Option B: Increments lifetime premium video savers used and preserves active streak count.
     * Ensure the parameter names inside @Param exactly match the text markers in the SQL block.
     */
    @Modifying
    @Query(value = """
        UPDATE user_streaks 
        SET total_video_savers_used = total_video_savers_used + 1,
            current_streak = :currentStreak,
            last_completed_date = CURRENT_DATE - INTERVAL '1 day',
            updated_at = NOW()
        WHERE user_id = :userId
        """, nativeQuery = true)
    int consumePremiumVideoSaver(
            @Param("userId") Long userId,
            @Param("currentStreak") int currentStreak
    );


    /**
     * Option C: Completely resets current streak metric or resolves state boundaries cleanly.
     */
    @Modifying
    @Query(value = """
        UPDATE user_streaks 
        SET current_streak = :targetStreakValue,
            last_completed_date = :targetCompletedDate,
            updated_at = NOW()
        WHERE user_id = :userId
        """, nativeQuery = true)
    void resolveManualStreakState(
            @Param("userId") Long userId,
            @Param("targetStreakValue") int targetStreakValue,
            @Param("targetCompletedDate") LocalDate targetCompletedDate
    );

    // TODO they can hold more than one shield. probably can make more money from that
    // market place to buy shields with cash or to exchange them for coins??? probably not
    @Modifying
    @Query(value = """
        UPDATE user_streaks us
        SET streak_shields = us.streak_shields + 1,
            total_shields_earned = us.total_shields_earned + 1,
            updated_at = NOW()
        FROM daily_stats ds
        WHERE us.user_id = ds.user_id
          AND ds.date = CURRENT_DATE
          AND ds.total_steps >= 15000
          -- Failsafe: Ensure we haven't already updated their inventory for today's 15k mark
          AND us.updated_at::date < CURRENT_DATE;
        """, nativeQuery = true)
    int awardEarnedShieldsFromDailyStats();


}


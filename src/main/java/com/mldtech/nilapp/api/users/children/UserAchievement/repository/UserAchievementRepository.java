package com.mldtech.nilapp.api.users.children.UserAchievement.repository;


import com.mldtech.nilapp.api.users.children.UserAchievement.model.UserAchievement;
import com.mldtech.nilapp.api.users.dto.AchievementCountDTO;
import com.mldtech.nilapp.api.users.dto.AchievementDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface UserAchievementRepository extends JpaRepository<UserAchievement, Long> {
//    List<UserAchievement> findByUserAchievementByUserId(Long userId);
    List<UserAchievement> findUserAchievementByUserUserId(Long userId);
//    List<UserAchievement> findUserAchievementByUserUserIdAndEarnedAt_Month(Long userId);
//    boolean existsByUserUserIdAndAchievementId(Long userId, Long achievementId);

    /**
     * Counts how many times each achievement was earned by a user within a date range.
     * Returns the achievement ID, name, total count, and last earned date.
     */
    @Query("""
        SELECT new com.mldtech.nilapp.api.users.dto.AchievementCountDTO(
            ua.achievement.achievementId,
            ua.achievement.achievement,
            COUNT(ua),
            MAX(ua.earnedAt),
            null,
            null,
            MIN(ua.earnedAt),
            ua.achievement.description,
            ua.achievement.badge
        )
        FROM UserAchievement ua
        WHERE ua.user.userId = :userId
          AND ua.earnedAt BETWEEN :start AND :end
        GROUP BY 
            ua.achievement.achievementId,
            ua.achievement.achievement,
            ua.achievement.description,
            ua.achievement.badge
    """)
    List<AchievementCountDTO> getUserAchievementsWithCount(
            Long userId,
            LocalDateTime start,
            LocalDateTime end
    );

    List<UserAchievement> findByUser_UserId(Long userId);

    /**
     * Resolves user records by alphanumeric Username.
     * Uses explicit type casting for null parameters to satisfy Hibernate validation.
     */
// ======================================================================
    // 🔍 SEARCH BY USERNAME
    // ======================================================================
    @Query("""
        SELECT new com.mldtech.nilapp.api.users.dto.AchievementCountDTO(
            ua.achievement.achievementId,
            ua.achievement.achievement,
            COUNT(ua),
            MAX(ua.earnedAt),
            null,
            null,
            MIN(ua.earnedAt),
            ua.achievement.description,
            ua.achievement.badge
        )
        FROM UserAchievement ua
        WHERE ua.user.username = :username
          AND ua.earnedAt BETWEEN :start AND :end
        GROUP BY 
            ua.achievement.achievementId,
            ua.achievement.achievement,
            ua.achievement.description,
            ua.achievement.badge
    """)
    List<AchievementCountDTO> getUserAchievementsWithCountByUsername(
            @Param("username") String username,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}


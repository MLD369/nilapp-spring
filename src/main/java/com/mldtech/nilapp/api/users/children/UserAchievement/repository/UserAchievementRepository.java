package com.mldtech.nilapp.api.users.children.UserAchievement.repository;


import com.mldtech.nilapp.api.users.children.UserAchievement.model.UserAchievement;
import com.mldtech.nilapp.api.users.dto.AchievementCountDTO;
import com.mldtech.nilapp.api.users.dto.AchievementDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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
        MIN(ua.earnedAt)
    )
    FROM UserAchievement ua
    WHERE ua.user.userId = :userId
      AND ua.earnedAt BETWEEN :start AND :end
    GROUP BY 
        ua.achievement.achievementId,
        ua.achievement.achievement
""")
    List<AchievementCountDTO> getUserAchievementsWithCount(
            Long userId,
            LocalDateTime start,
            LocalDateTime end
    );

    List<UserAchievement> findByUser_UserId(Long userId);

}


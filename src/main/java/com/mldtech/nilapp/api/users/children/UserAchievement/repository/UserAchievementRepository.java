package com.mldtech.nilapp.api.users.children.UserAchievement.repository;


import com.mldtech.nilapp.api.users.children.UserAchievement.model.UserAchievement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserAchievementRepository extends JpaRepository<UserAchievement, Long> {
//    List<UserAchievement> findByUserAchievementByUserId(Long userId);
    List<UserAchievement> findUserAchievementByUserUserId(Long userId);
//    List<UserAchievement> findUserAchievementByUserUserIdAndEarnedAt_Month(Long userId);
//    boolean existsByUserUserIdAndAchievementId(Long userId, Long achievementId);
}


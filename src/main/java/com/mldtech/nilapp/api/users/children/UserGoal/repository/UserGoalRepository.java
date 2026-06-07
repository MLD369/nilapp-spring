package com.mldtech.nilapp.api.users.children.UserGoal.repository;


import com.mldtech.nilapp.api.users.children.UserGoal.model.UserGoal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserGoalRepository extends JpaRepository<UserGoal, Long> {
    List<UserGoal> findByUserUserIdAndIsActiveTrue(Long userId);


    // Get all completed goals for a user within a date range
    List<UserGoal> findByUserUserIdAndIsCompleteTrueAndCompletedAtBetween(
            Long userId,
            LocalDateTime start,
            LocalDateTime end
    );
    List<UserGoal> findByUser_UserId(Long userId);
    boolean existsByUser_UserIdAndGoalInstance_GoalInstanceIdAndLeftAtIsNull(Long userId, Long instanceId);
    Optional<UserGoal> findByUser_UserIdAndGoalInstance_GoalInstanceIdAndLeftAtIsNull(Long userId, Long instanceId);

}

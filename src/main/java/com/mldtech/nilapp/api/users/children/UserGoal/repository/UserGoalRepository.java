package com.mldtech.nilapp.api.users.children.UserGoal.repository;


import com.mldtech.nilapp.api.users.children.UserGoal.model.UserGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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


    /**
     * Checks if a user is actively participating in a specific goal challenge right now.
     */
    boolean existsByUser_UserIdAndGoalInstance_GoalInstanceIdAndIsActiveTrue(Long userId, Long goalInstanceId);

    /**
     * Finds the user's currently active row for a specific challenge instance.
     */
    Optional<UserGoal> findByUser_UserIdAndGoalInstance_GoalInstanceIdAndIsActiveTrue(Long userId, Long goalInstanceId);
    @Modifying
    @Query(value = """
        INSERT INTO user_goals (user_id, goal_instance_id, target_type, target_amount, allocation_pct, is_active, is_complete, joined_at)
        SELECT :userId, :goalInstanceId, :targetType, :targetAmount, :allocationPct, true, false, NOW()
        WHERE NOT EXISTS (
            SELECT 1 FROM user_goals 
            WHERE user_id = :userId 
              AND goal_instance_id = :goalInstanceId 
              AND is_active = true
        );
        """, nativeQuery = true)
    int manuallyRejoinGoalChallenge(
            @Param("userId") Long userId,
            @Param("goalInstanceId") Long goalInstanceId,
            @Param("targetType") String targetType,
            @Param("targetAmount") int targetAmount,
            @Param("allocationPct") double allocationPct
    );

    @Modifying
    @Query(value = """
    UPDATE public.user_goals 
    SET user_goal_status_id = (SELECT user_goal_status_id FROM public.user_goal_statuses WHERE code = :statusCode)
    WHERE user_goal_id = :id
    """, nativeQuery = true)
    void updateUserGoalStatusByCode(@Param("id") Long id, @Param("statusCode") String statusCode);

    /**
     * Locates all child user goal contract primary keys tied to a parent challenge instance.
     *
     * @param goalInstanceId the parent instance identifier
     * @return a {@link List} of {@link Long} user goal IDs
     */
    @Query(value = """
   SELECT user_goal_id 
   FROM public.user_goals 
   WHERE goal_instance_id = :goalInstanceId
   """, nativeQuery = true)
    List<Long> findUserGoalIdsByInstanceId(@Param("goalInstanceId") Long goalInstanceId);

}

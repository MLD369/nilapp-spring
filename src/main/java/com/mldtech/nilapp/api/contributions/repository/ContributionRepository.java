package com.mldtech.nilapp.api.contributions.repository;

import com.mldtech.nilapp.api.contributions.dto.ContributionSummaryDTO;
import com.mldtech.nilapp.api.contributions.dto.ContributionTotalDTO;
import com.mldtech.nilapp.api.contributions.model.Contribution;
import com.mldtech.nilapp.api.users.children.UserEntity.model.UserEntity;
import com.mldtech.nilapp.api.users.children.UserGroup.model.UserGroup;
import com.mldtech.nilapp.api.users.dto.EntityNilLeaderDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ContributionRepository extends JpaRepository<Contribution, Long> {

    List<Contribution> findByUserId(Long userId);

    List<Contribution> findByEntityId(Long entityId);

    List<Contribution> findByCampaignId(Long campaignId);

//    List<Contribution> findByStatusId(Integer statusId);
    List<Contribution> findByUserIdAndEntityId(Long userId, Long entityId);

    // TODO update the view to get the  entity or group name
    @Query(value =
            "SELECT month_label, year_label, entity_id, total_steps, total_coins " +
                    " FROM user_entity_stats_view " +
                    " WHERE user_id = :userId " +
                    "  AND (:entityId IS NULL OR entity_id = :entityId) " +
                    "  AND contribution_status_id = :statusId " +
                    "  AND created_at BETWEEN :startDate AND :endDate " +
                    " ORDER BY month_label, year_label",
            nativeQuery = true)
    List<Object[]> getEntityStats(
            @Param("userId") Long userId,
            @Param("entityId") Long entityId,
            @Param("statusId") Long statusId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
    // TODO update the view to get the  entity or group name
    @Query(value =
            "SELECT month_label, year_label, group_id, total_steps, total_coins " +
                    " FROM user_group_stats_view " +
                    " WHERE user_id = :userId " +
                    "  AND (:groupId IS NULL OR group_id = :groupId) " +
                    "  AND contribution_status_id = :statusId " +
                    "  AND created_at BETWEEN :startDate AND :endDate " +
                    " ORDER BY month_label, year_label",
            nativeQuery = true)
    List<Object[]> getGroupStats(
            @Param("userId") Long userId,
            @Param("groupId") Long groupId,
            @Param("statusId") Long statusId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("""
    SELECT new com.mldtech.nilapp.api.contributions.dto.ContributionTotalDTO(
        COALESCE(SUM(c.stepsContributed), 0),
        COALESCE(SUM(c.coinsContributed), 0)
    )
    FROM Contribution c
    WHERE c.userId = :userId
      AND c.createdAt BETWEEN :start AND :end
""")
    ContributionTotalDTO getUserMonthlyTotals(
            Long userId,
            LocalDateTime start,
            LocalDateTime end
    );

    @Query("""
    SELECT u.userId, u.username, SUM(c.coinsContributed)
    FROM Contribution c
    JOIN User u ON u.userId = c.userId
    WHERE c.createdAt BETWEEN :start AND :end
    GROUP BY u.userId, u.username
    ORDER BY SUM(c.coinsContributed) DESC
""")
    List<Object[]> getTopCoins(LocalDateTime start, LocalDateTime end);

    @Query("""
    SELECT new com.mldtech.nilapp.api.users.dto.EntityNilLeaderDTO(
        e.entityId,
        e.name,
        SUM(c.coinsContributed)
    )
    FROM Contribution c
    JOIN Entities e ON e.entityId = c.entityId
    WHERE c.createdAt BETWEEN :start AND :end
    GROUP BY e.entityId, e.name
    ORDER BY SUM(c.coinsContributed) DESC
""")
    List<EntityNilLeaderDTO> getEntityNilLeaderboard(LocalDateTime start, LocalDateTime end);

    @Query("""
    SELECT c FROM Contribution c
    WHERE c.userId = :userId
      AND c.createdAt >= :start
      AND c.createdAt < :end
""")
    List<Contribution> findByUserIdAndDateRange(Long userId, LocalDateTime start, LocalDateTime end);

    @Modifying
    @Query(value = """
        WITH locked_contributions AS (
            SELECT contribution_id, user_id, steps_contributed, coins_contributed, created_at, allocation_snapshot
            FROM contributions
            WHERE contribution_status_id = 1
            FOR UPDATE SKIP LOCKED
        ),
        inserted_entities AS (
            INSERT INTO entity_stats (contribution_id, entity_id, user_id, steps, coins, allocation_percentage, created_at)
            SELECT 
                lc.contribution_id,
                (alloc->>'entityId')\\:\\:bigint,
                lc.user_id,
                ROUND((lc.steps_contributed * (alloc->>'percentage')\\:\\:numeric) / 100)\\:\\:bigint,
                ROUND((lc.coins_contributed * (alloc->>'percentage')\\:\\:numeric) / 100)\\:\\:bigint,
                (alloc->>'percentage')\\:\\:int,
                lc.created_at
            FROM locked_contributions lc
            CROSS JOIN LATERAL jsonb_array_elements(lc.allocation_snapshot->'entityAllocations') AS alloc
        ),
        inserted_groups AS (
            INSERT INTO group_stats (contribution_id, group_id, user_id, steps, coins, allocation_percentage, created_at)
            SELECT 
                lc.contribution_id,
                (alloc->>'groupId')\\:\\:bigint,
                lc.user_id,
                ROUND((lc.steps_contributed * (alloc->>'percentage')\\:\\:numeric) / 100)\\:\\:bigint,
                ROUND((lc.coins_contributed * (alloc->>'percentage')\\:\\:numeric) / 100)\\:\\:bigint,
                (alloc->>'percentage')\\:\\:int,
                lc.created_at
            FROM locked_contributions lc
            CROSS JOIN LATERAL jsonb_array_elements(lc.allocation_snapshot->'groupAllocations') AS alloc
        )
        UPDATE contributions
        SET contribution_status_id = 2
        WHERE contribution_id IN (SELECT contribution_id FROM locked_contributions)
        """, nativeQuery = true)
    int processPendingContributions();

    // Replace your evaluateEntityGoals and evaluateGroupGoals methods with these:

    @Modifying
    @Query(value = """
        UPDATE goal_instances gi
        SET 
            steps_contributed = gi.steps_contributed + COALESCE((
                SELECT SUM(es.steps) 
                FROM entity_stats es
                JOIN contributions c ON es.contribution_id = c.contribution_id
                WHERE es.entity_id = gi.entity_id AND c.contribution_status_id = 1
            ), 0),
            coins_contributed = gi.coins_contributed + COALESCE((
                SELECT SUM(es.coins) 
                FROM entity_stats es
                JOIN contributions c ON es.contribution_id = c.contribution_id
                WHERE es.entity_id = gi.entity_id AND c.contribution_status_id = 1
            ), 0),
            is_complete = CASE 
                WHEN (gi.coins_contributed + COALESCE((SELECT SUM(es.coins) FROM entity_stats es JOIN contributions c ON es.contribution_id = c.contribution_id WHERE es.entity_id = gi.entity_id AND c.contribution_status_id = 1), 0)) >= gi.coins_needed THEN true 
                ELSE gi.is_complete 
            END,
            is_active = CASE 
                WHEN (gi.coins_contributed + COALESCE((SELECT SUM(es.coins) FROM entity_stats es JOIN contributions c ON es.contribution_id = c.contribution_id WHERE es.entity_id = gi.entity_id AND c.contribution_status_id = 1), 0)) >= gi.coins_needed THEN false 
                ELSE gi.is_active 
            END,
            completed_at = CASE 
                WHEN (gi.coins_contributed + COALESCE((SELECT SUM(es.coins) FROM entity_stats es JOIN contributions c ON es.contribution_id = c.contribution_id WHERE es.entity_id = gi.entity_id AND c.contribution_status_id = 1), 0)) >= gi.coins_needed AND gi.is_complete = false THEN NOW() 
                ELSE gi.completed_at 
            END
        WHERE gi.is_active = true AND gi.entity_id IS NOT NULL;
        """, nativeQuery = true)
    int evaluateEntityGoals();

    @Modifying
    @Query(value = """
        UPDATE goal_instances gi
        SET 
            steps_contributed = gi.steps_contributed + COALESCE((
                SELECT SUM(gs.steps) 
                FROM group_stats gs
                JOIN contributions c ON gs.contribution_id = c.contribution_id
                WHERE gs.group_id = gi.group_id AND c.contribution_status_id = 1
            ), 0),
            coins_contributed = gi.coins_contributed + COALESCE((
                SELECT SUM(gs.coins) 
                FROM group_stats gs
                JOIN contributions c ON gs.contribution_id = c.contribution_id
                WHERE gs.group_id = gi.group_id AND c.contribution_status_id = 1
            ), 0),
            is_complete = CASE 
                WHEN (gi.coins_contributed + COALESCE((SELECT SUM(gs.coins) FROM group_stats gs JOIN contributions c ON gs.contribution_id = c.contribution_id WHERE gs.group_id = gi.group_id AND c.contribution_status_id = 1), 0)) >= gi.coins_needed THEN true 
                ELSE gi.is_complete 
            END,
            is_active = CASE 
                WHEN (gi.coins_contributed + COALESCE((SELECT SUM(gs.coins) FROM group_stats gs JOIN contributions c ON gs.contribution_id = c.contribution_id WHERE gs.group_id = gi.group_id AND c.contribution_status_id = 1), 0)) >= gi.coins_needed THEN false 
                ELSE gi.is_active 
            END,
            completed_at = CASE 
                WHEN (gi.coins_contributed + COALESCE((SELECT SUM(gs.coins) FROM group_stats gs JOIN contributions c ON gs.contribution_id = c.contribution_id WHERE gs.group_id = gi.group_id AND c.contribution_status_id = 1), 0)) >= gi.coins_needed AND gi.is_complete = false THEN NOW() 
                ELSE gi.completed_at 
            END
        WHERE gi.is_active = true AND gi.group_id IS NOT NULL;
        """, nativeQuery = true)
    int evaluateGroupGoals();

    @Modifying
    @Query(value = """
        UPDATE user_goals ug
        SET 
            is_complete = true,
            is_active = false,
            completed_at = gi.completed_at
        FROM goal_instances gi
        WHERE ug.goal_instance_id = gi.goal_instance_id
          AND gi.is_complete = true 
          AND ug.is_active = true;
        """, nativeQuery = true)
    int finalizeUserGoalsFromInstances();

    // Gives the user achievements
    @Modifying
    @Query(value = """
        INSERT INTO user_achievements (user_id, achievement_id, earned_at)
        SELECT es.user_id, a.achievement_id, NOW()
        FROM entity_stats es
        CROSS JOIN achievements a
        GROUP BY es.user_id, a.achievement_id, a.required_steps
        HAVING SUM(es.steps) >= a.required_steps
        ON CONFLICT (user_id, achievement_id, earned_at) DO NOTHING;
        """, nativeQuery = true)
    int assignUserAchievements();


/*

API to allocate claims to groups and entities - done
change contributions - done

	Api to check entity goals - ?
	Api to check group goals - ?
	Api to assign achievements - ?


	Notification that goal or achievement reached


    Api to save total steps - once a day when user opens app
	Api to save user streak - once a day when user opens app
	shield /streak saver
  */
}


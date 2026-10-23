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


    /**
     * Retrieves all master contributions logged by a specific user.
     *
     * @param userId the unique tracking identifier of the user
     * @return a list of contributions matching the provided user ID
     */
    List<Contribution> findByUserId(Long userId);

    /**
     * Retrieves all contributions allocated to a specific corporate entity or school organization.
     *
     * @param entityId the unique tracking identifier of the targeted organization
     * @return a list of contributions associated with the given entity ID
     */
    List<Contribution> findByEntityId(Long entityId);

    /**
     * Retrieves all contributions registered under an active marketing or sponsorship campaign.
     *
     * @param campaignId the tracking identifier of the sponsor campaign
     * @return a list of contributions tied to the specified campaign
     */
    List<Contribution> findByCampaignId(Long campaignId);

//    List<Contribution> findByStatusId(Integer statusId);

    /**
     * Retrieves a scoped list of contributions made by an individual user to one specific entity.
     *
     * @param userId the unique tracking identifier of the user
     * @param entityId the unique tracking identifier of the entity
     * @return a list of contributions matching both criteria parameters
     */
    List<Contribution> findByUserIdAndEntityId(Long userId, Long entityId);

    // TODO update the view to get the  entity or group name
    /**
     * Fetches monthly aggregated historical steps and coins data allocated to entities from a database view.
     * Supports optional single-entity filtering and strict timestamp range auditing.
     *
     * @param userId the unique tracking identifier of the user whose data is being queried
     * @param entityId optional identifier to filter results down to one target entity (pass null for all)
     * @param statusId the verification status ID constraint to check (e.g., PENDING or APPROVED)
     * @param startDate the starting timeline boundary of the search window
     * @param endDate the concluding timeline boundary of the search window
     * @return a list of unmapped object arrays containing month labels, year labels, IDs, steps, and coins totals
     */
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
    /**
     * Fetches monthly aggregated historical steps and coins data allocated to friend groups from a database view.
     * Supports optional single-group filtering and strict timestamp range auditing.
     *
     * @param userId the unique tracking identifier of the user whose data is being queried
     * @param groupId optional identifier to filter results down to one target group (pass null for all)
     * @param statusId the verification status ID constraint to check (e.g., PENDING or APPROVED)
     * @param startDate the starting timeline boundary of the search window
     * @param endDate the concluding timeline boundary of the search window
     * @return a list of unmapped object arrays containing month labels, year labels, IDs, steps, and coins totals
     */
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

    /**
     * Aggregates a single user's total steps and coins generated across a custom monthly date range.
     * Failsafe protection encapsulates null returns by converting empty values to 0.
     *
     * @param userId the tracking identifier of the target user
     * @param start the open date threshold boundary
     * @param end the close date threshold boundary
     * @return a data transfer object housing the running monthly step and coin values combined
     */
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

    /**
     * Generates a global leaderboard list ranking platform users by total coins earned within a specific window.
     * Sorts descending to ensure the highest contributors appear at the top.
     *
     * @param start the beginning limit of the competition timeline window
     * @param end the absolute concluding edge limit of the competition timeline window
     * @return a list of object arrays returning user IDs, usernames, and coin sum aggregates
     */
    @Query("""
    SELECT u.userId, u.username, SUM(c.coinsContributed)
    FROM Contribution c
    JOIN User u ON u.userId = c.userId
    WHERE c.createdAt BETWEEN :start AND :end
    GROUP BY u.userId, u.username
    ORDER BY SUM(c.coinsContributed) DESC
""")
    List<Object[]> getTopCoins(LocalDateTime start, LocalDateTime end);

    /**
     * Generates an institution leaderboard ranking organizations by total coins received over a given date range.
     * Sorts descending to reveal the top performing organizational profiles.
     *
     * @param start the open parameter limit of the auditing time window
     * @param end the closing parameter limit of the auditing time window
     * @return a compiled list of structured leaderboard data transfer entities matching active metrics
     */
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

    /**
     * Queries individual user contribution records bounded strictly within custom timestamp parameters.
     *
     * @param userId the unique tracking identifier of the target user
     * @param start the inclusive starting timestamp parameter boundary
     * @param end the exclusive ending timestamp parameter boundary
     * @return a collection list containing uncompressed raw contribution models
     */
    @Query("""
    SELECT c FROM Contribution c
    WHERE c.userId = :userId
      AND c.createdAt >= :start
      AND c.createdAt < :end
""")
    List<Contribution> findByUserIdAndDateRange(Long userId, LocalDateTime start, LocalDateTime end);


    /**
     * Executes the background metrics distribution fan-out process.
     * Grabs un-batched pending contributions (status 1) using row-level locking to prevent concurrency collisions,
     * flattens nested JSON parameters inside the allocation snapshot, splits running metrics to separate flat statistics tables,
     * calculates precise organization payouts (70%) and platform margins (30%), and transitions processed contribution records
     * to status 2 (Approved) in a single atomic transaction.
     *
     * @return the total number of pending master contribution rows successfully processed and approved
     */
    @Modifying
    @Query(value = """
        WITH locked_contributions AS (
            SELECT contribution_id, user_id, steps_contributed, coins_contributed, conversion_rate, created_at, allocation_snapshot
            FROM contributions
            WHERE contribution_status_id = 1
            FOR UPDATE SKIP LOCKED
        ),
        inserted_entities AS (
            INSERT INTO entity_stats (contribution_id, entity_id, user_id, steps, coins, allocation_percentage, revenue_generated, company_profit_retained, created_at)
            SELECT 
                lc.contribution_id,
                (alloc->>'entityId')\\:\\:bigint,
                lc.user_id,
                ROUND((lc.steps_contributed * (alloc->>'percentage')\\:\\:numeric) / 100)\\:\\:bigint,
                ROUND((lc.coins_contributed * (alloc->>'percentage')\\:\\:numeric) / 100)\\:\\:bigint,
                (alloc->>'percentage')\\:\\:int,
                (((lc.coins_contributed * lc.conversion_rate) * (alloc->>'percentage')\\:\\:numeric / 100) * 0.70)\\:\\:decimal(15,6),
                (((lc.coins_contributed * lc.conversion_rate) * (alloc->>'percentage')\\:\\:numeric / 100) * 0.30)\\:\\:decimal(15,6),
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

    /**
     * Evaluates all active entity-level campaign initiatives against current contribution batch values.
     * Increments cumulative steps, coins, and precise dollar totals. If the current coin volume meets or
     * exceeds the target coins required, it concludes the campaign timeline automatically by shifting
     * the records to an inactive state, logging the completion timestamp, and flagging the pacing tracker
     * as complete for audit verification.
     *
     * @return the total number of active entity goal instances modified or completed
     */
    @Modifying
    @Query(value = """
        UPDATE goal_instances gi
        SET 
            steps_contributed = gi.steps_contributed + COALESCE((SELECT SUM(es.steps) FROM entity_stats es JOIN contributions c ON es.contribution_id = c.contribution_id WHERE es.entity_id = gi.entity_id AND c.contribution_status_id = 1), 0),
            coins_contributed = gi.coins_contributed + COALESCE((SELECT SUM(es.coins) FROM entity_stats es JOIN contributions c ON es.contribution_id = c.contribution_id WHERE es.entity_id = gi.entity_id AND c.contribution_status_id = 1), 0),
            dollar_amount = gi.dollar_amount + COALESCE((SELECT SUM(es.revenue_generated) FROM entity_stats es JOIN contributions c ON es.contribution_id = c.contribution_id WHERE es.entity_id = gi.entity_id AND c.contribution_status_id = 1), 0.000000),
            is_complete = CASE WHEN (gi.coins_contributed + COALESCE((SELECT SUM(es.coins) FROM entity_stats es JOIN contributions c ON es.contribution_id = c.contribution_id WHERE es.entity_id = gi.entity_id AND c.contribution_status_id = 1), 0)) >= gi.coins_needed THEN true ELSE gi.is_complete END,
            is_active = CASE WHEN (gi.coins_contributed + COALESCE((SELECT SUM(es.coins) FROM entity_stats es JOIN contributions c ON es.contribution_id = c.contribution_id WHERE es.entity_id = gi.entity_id AND c.contribution_status_id = 1), 0)) >= gi.coins_needed THEN false ELSE gi.is_active END,
            completed_at = CASE WHEN (gi.coins_contributed + COALESCE((SELECT SUM(es.coins) FROM entity_stats es JOIN contributions c ON es.contribution_id = c.contribution_id WHERE es.entity_id = gi.entity_id AND c.contribution_status_id = 1), 0)) >= gi.coins_needed AND gi.is_complete = false THEN NOW() ELSE gi.completed_at END,
            pacing_status = CASE WHEN (gi.coins_contributed + COALESCE((SELECT SUM(es.coins) FROM entity_stats es JOIN contributions c ON es.contribution_id = c.contribution_id WHERE es.entity_id = gi.entity_id AND c.contribution_status_id = 1), 0)) >= gi.coins_needed THEN 'CLOSED_PENDING_AUDIT' ELSE gi.pacing_status END,
            -- STAMP UPDATED TIME ON WRITE RUNS:
            updated_at = NOW()
        WHERE gi.is_active = true AND gi.entity_id IS NOT NULL
        """, nativeQuery = true)
    int evaluateEntityGoals();

    /**
     * Evaluates all active friend group social goals against current contribution metrics.
     * Increments running step and coin pools mapped to group stats records. If the aggregated coins pool
     * hits or exceeds the goal instance target threshold, the method transitions the challenge state to
     * finished by flagging completion, locking modifications, and updating the final resolution timestamp.
     *
     * @return the total number of active group challenge milestones updated or concluded
     */
    @Modifying
    @Query(value = """
        UPDATE goal_instances gi
        SET 
            steps_contributed = gi.steps_contributed + COALESCE((SELECT SUM(gs.steps) FROM group_stats gs JOIN contributions c ON gs.contribution_id = c.contribution_id WHERE gs.group_id = gi.group_id AND c.contribution_status_id = 1), 0),
            coins_contributed = gi.coins_contributed + COALESCE((SELECT SUM(gs.coins) FROM group_stats gs JOIN contributions c ON gs.contribution_id = c.contribution_id WHERE gs.group_id = gi.group_id AND c.contribution_status_id = 1), 0),
            is_complete = CASE WHEN (gi.coins_contributed + COALESCE((SELECT SUM(gs.coins) FROM group_stats gs JOIN contributions c ON gs.contribution_id = c.contribution_id WHERE gs.group_id = gi.group_id AND c.contribution_status_id = 1), 0)) >= gi.coins_needed THEN true ELSE gi.is_complete END,
            is_active = CASE WHEN (gi.coins_contributed + COALESCE((SELECT SUM(gs.coins) FROM group_stats gs JOIN contributions c ON gs.contribution_id = c.contribution_id WHERE gs.group_id = gi.group_id AND c.contribution_status_id = 1), 0)) >= gi.coins_needed THEN false ELSE gi.is_active END,
            completed_at = CASE WHEN (gi.coins_contributed + COALESCE((SELECT SUM(gs.coins) FROM group_stats gs JOIN contributions c ON gs.contribution_id = c.contribution_id WHERE gs.group_id = gi.group_id AND c.contribution_status_id = 1), 0)) >= gi.coins_needed AND gi.is_complete = false THEN NOW() ELSE gi.completed_at END,
            -- STAMP UPDATED TIME ON WRITE RUNS:
            updated_at = NOW()
        WHERE gi.is_active = true AND gi.group_id IS NOT NULL
        """, nativeQuery = true)
    int evaluateGroupGoals();

    /**
     * Calibrates and logs dynamic, tiered pacing metrics for active campaign goals.
     * Evaluates the exact duration percentage elapsed against corresponding coin milestones (25%, 50%, 75%, 80%, 90%)
     * to surface granular performance flags, allowing admins to adjust configurations before deadlines fail.
     *
     * @return the absolute integer count of challenge instances whose pacing statuses were recalibrated
     */
    @Modifying
    @Query(value = """
        UPDATE goal_instances
        SET pacing_status = CASE 
                WHEN NOW() > (start_date + ((end_date - start_date) * INTERVAL '1 day' * 0.90)) AND coins_contributed < (coins_needed * 0.88) THEN 'NEAR_EXPIRATION_ALERT'
                WHEN NOW() > (start_date + ((end_date - start_date) * INTERVAL '1 day' * 0.80)) AND coins_contributed < (coins_needed * 0.75) THEN 'HIGH_RISK_FAILURE'
                WHEN NOW() > (start_date + ((end_date - start_date) * INTERVAL '1 day' * 0.75)) AND coins_contributed < (coins_needed * 0.65) THEN 'CRITICAL_LAGGING'
                WHEN NOW() > (start_date + ((end_date - start_date) * INTERVAL '1 day' * 0.50)) AND coins_contributed < (coins_needed * 0.40) THEN 'BEHIND_NEED_RECALCULATION'
                WHEN NOW() > (start_date + ((end_date - start_date) * INTERVAL '1 day' * 0.25)) AND coins_contributed < (coins_needed * 0.15) THEN 'SLIGHTLY_BEHIND'
                ELSE 'ON_TRACK'
            END,
            -- STAMP UPDATED TIME ON WRITE RUNS:
            updated_at = NOW()
        WHERE is_active = true AND pacing_status NOT IN ('CLOSED_PENDING_AUDIT', 'SETTLED')
        """, nativeQuery = true)
    int recalculateGoalPacingStatuses();

    @Modifying
    @Query(value = """
        UPDATE user_goals ug
        SET is_complete = true,
            is_active = false,
            completed_at = gi.completed_at
        FROM goal_instances gi
        WHERE ug.goal_instance_id = gi.goal_instance_id
          AND gi.is_complete = true 
          AND ug.is_active = true
        """, nativeQuery = true)
    int finalizeUserGoalsFromInstances();

    // Gives the user achievements
    /**
     * Evaluates organizational statistics records against system milestone parameters to award achievements.
     * Aggregates total steps per user across entity stats to identify who qualifies for milestone badges;
     * writes new credentials into the ledger while utilizing a timestamp constraint index to bypass duplicate runs.
     *
     * @return the absolute count of new organizational achievements safely earned and logged [3]
     */
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

    /**
     * Evaluates and updates independent, user-driven individual goal tasks.
     * Aggregates newly approved ad claim metrics from the current date to update step or coin progress columns.
     * If a target threshold is reached, it closes the record, locks future updates, logs the resolution timestamp,
     * and instantly mints a standard individual achievement badge using a unique timestamp index loop.
     *
     * @return the number of separate user goal rows successfully processed during this batch iteration [3]
     */
    @Modifying
    @Query(value = """
        WITH calculated_progress AS (
            SELECT 
                c.user_id,
                SUM(c.steps_contributed) AS daily_steps,
                SUM(c.coins_contributed) AS daily_coins
            FROM contributions c
            WHERE c.contribution_status_id = 2 -- APPROVED
              AND DATE_TRUNC('day', c.created_at) = CURRENT_DATE
            GROUP BY c.user_id
        ),
        updated_user_goals AS (
            UPDATE user_goals ug
            SET 
                current_steps_progress = ug.current_steps_progress + COALESCE(cp.daily_steps, 0),
                current_coins_progress = ug.current_coins_progress + COALESCE(cp.daily_coins, 0),
                -- Close the goal immediately if they cross the target coin or step threshold
                is_complete = CASE 
                    WHEN ug.target_type = 'COINS' AND (ug.current_coins_progress + COALESCE(cp.daily_coins, 0)) >= ug.target_amount THEN true
                    WHEN ug.target_type = 'STEPS' AND (ug.current_steps_progress + COALESCE(cp.daily_steps, 0)) >= ug.target_amount THEN true
                    ELSE false 
                END,
                is_active = CASE 
                    WHEN ug.target_type = 'COINS' AND (ug.current_coins_progress + COALESCE(cp.daily_coins, 0)) >= ug.target_amount THEN false
                    WHEN ug.target_type = 'STEPS' AND (ug.current_steps_progress + COALESCE(cp.daily_steps, 0)) >= ug.target_amount THEN false
                    ELSE true 
                END,
                completed_at = CASE 
                    WHEN ug.completed_at IS NULL AND (
                        (ug.target_type = 'COINS' AND (ug.current_coins_progress + COALESCE(cp.daily_coins, 0)) >= ug.target_amount) OR
                        (ug.target_type = 'STEPS' AND (ug.current_steps_progress + COALESCE(cp.daily_steps, 0)) >= ug.target_amount)
                    ) THEN NOW()
                    ELSE ug.completed_at
                END
            FROM calculated_progress cp
            WHERE ug.user_id = cp.user_id AND ug.is_active = true
            RETURNING ug.user_id, ug.is_complete, ug.goal_instance_id
        )
        -- Mint a unique user achievement entry instantly for the completed row run
        INSERT INTO user_achievements (user_id, achievement_id, earned_at)
        SELECT uug.user_id, 101, NOW() -- 101 = Standard Individual Goal Cleared Badge ID
        FROM updated_user_goals uug
        WHERE uug.is_complete = true
        ON CONFLICT (user_id, achievement_id, earned_at) DO NOTHING;
        """, nativeQuery = true)
    int evaluateUserGoalsBatch();


}


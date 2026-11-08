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
import org.springframework.transaction.annotation.Transactional;

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
    /**
     * Executes the background metrics distribution fan-out process.
     * Grabs un-batched pending contributions (status 1) using row-level locking to prevent concurrency collisions,
     * flattens nested JSON parameters inside the allocation snapshot, splits running metrics to separate flat statistics tables,
     * calculates precise organization payouts (70%) and platform margins (30%) for entities, handles clean whole-number
     * remainder splits for both tables, and transitions processed contribution records to status 5 in a single atomic transaction.
     *
     * @return the total number of pending master contribution rows successfully processed and approved
     */
    @Modifying
    @Transactional
    @Query(value = """
        WITH locked_contributions AS (
            SELECT contribution_id, user_id, steps_contributed, coins_contributed, conversion_rate, created_at, allocation_snapshot
            FROM contributions
            WHERE contribution_status_id = 1 -- will need to change this to (2) verified once we have a verifcation process
            FOR UPDATE SKIP LOCKED
        ),
        -- 🏢 1. FLATTEN & CALCULATE SHARES FOR CORPORATE ENTITIES
        flattened_entities AS (
            SELECT 
                lc.contribution_id, lc.user_id, lc.created_at, lc.conversion_rate,
                CAST(CAST(alloc->>'entityId' AS numeric) AS bigint) AS entity_id,             -- 🌟 FIXED: Safe SQL function casts
                CAST(CAST(goal->>'goalInstanceId' AS numeric) AS bigint) AS goal_instance_id, -- 🌟 FIXED: Safe SQL function casts
                CAST(alloc->>'percentage' AS numeric) AS parent_pct,
                CAST(goal->>'share' AS numeric) AS goal_share,
                CAST(FLOOR((lc.steps_contributed * CAST(alloc->>'percentage' AS numeric)) / 100.0) AS bigint) AS total_entity_steps,
                CAST(FLOOR((lc.coins_contributed * CAST(alloc->>'percentage' AS numeric)) / 100.0) AS bigint) AS total_entity_coins,
                ROW_NUMBER() OVER (PARTITION BY lc.contribution_id, alloc->>'entityId' ORDER BY CAST(CAST(goal->>'goalInstanceId' AS numeric) AS bigint)) AS goal_rank,
                COUNT(*) OVER (PARTITION BY lc.contribution_id, alloc->>'entityId') AS total_goals_in_entity -- 🌟 FIXED: Added missing closing parenthesis
            FROM locked_contributions lc
            CROSS JOIN LATERAL jsonb_array_elements(lc.allocation_snapshot->'entityAllocations') AS alloc
            CROSS JOIN LATERAL jsonb_array_elements(alloc->'goals') AS goal
        ),
        -- 👥 2. FLATTEN & CALCULATE SHARES FOR FRIEND GROUPS (NO FINANCIAL DATA)
        flattened_groups AS (
            SELECT 
                lc.contribution_id, lc.user_id, lc.created_at,
                CAST(CAST(alloc->>'groupId' AS numeric) AS bigint) AS group_id,                     -- 🌟 FIXED: Safe SQL function casts
                CAST(CAST(goal->>'goalInstanceId' AS numeric) AS bigint) AS goal_instance_id,        -- 🌟 FIXED: Safe SQL function casts
                CAST(alloc->>'percentage' AS numeric) AS parent_pct,
                CAST(goal->>'share' AS numeric) AS goal_share,
                CAST(FLOOR((lc.steps_contributed * CAST(alloc->>'percentage' AS numeric)) / 100.0) AS bigint) AS total_group_steps,
                CAST(FLOOR((lc.coins_contributed * CAST(alloc->>'percentage' AS numeric)) / 100.0) AS bigint) AS total_group_coins,
                ROW_NUMBER() OVER (PARTITION BY lc.contribution_id, alloc->>'groupId' ORDER BY CAST(CAST(goal->>'goalInstanceId' AS numeric) AS bigint)) AS goal_rank,
                COUNT(*) OVER (PARTITION BY lc.contribution_id, alloc->>'groupId') AS total_goals_in_group
            FROM locked_contributions lc
            CROSS JOIN LATERAL jsonb_array_elements(lc.allocation_snapshot->'groupAllocations') AS alloc
            CROSS JOIN LATERAL jsonb_array_elements(alloc->'goals') AS goal
        ),
        -- 🏢 3. EXECUTE BULK WRITE TO ENTITY LEDGER (WITH FINANCIALS)
        inserted_entities AS (
            INSERT INTO entity_stats (contribution_id, entity_id, goal_instance_id, user_id, steps, coins, allocation_percentage, revenue_generated, company_profit_retained, created_at)
            SELECT 
                fe.contribution_id, fe.entity_id, fe.goal_instance_id, fe.user_id,
                CASE WHEN fe.goal_rank = fe.total_goals_in_entity THEN fe.total_entity_steps - CAST(FLOOR(fe.total_entity_steps * (1.0 - fe.goal_share)) AS bigint) ELSE CAST(FLOOR(fe.total_entity_steps * fe.goal_share) AS bigint) END,
                CASE WHEN fe.goal_rank = fe.total_goals_in_entity THEN fe.total_entity_coins - CAST(FLOOR(fe.total_entity_coins * (1.0 - fe.goal_share)) AS bigint) ELSE CAST(FLOOR(fe.total_entity_coins * fe.goal_share) AS bigint) END,
                CAST(fe.parent_pct AS int),
                CAST((((fe.total_entity_coins * fe.goal_share) * fe.conversion_rate) * 0.70) AS decimal(15,6)),
                CAST((((fe.total_entity_coins * fe.goal_share) * fe.conversion_rate) * 0.30) AS decimal(15,6)),
                fe.created_at
            FROM flattened_entities fe
            WHERE fe.goal_share > 0
        ),
        -- 👥 4. EXECUTE BULK WRITE TO GROUP LEDGER (NO FINANCIALS)
        inserted_groups AS (
            INSERT INTO group_stats (contribution_id, group_id, goal_instance_id, user_id, steps, coins, allocation_percentage, created_at)
            SELECT 
                fg.contribution_id, fg.group_id, fg.goal_instance_id, fg.user_id,
                CASE WHEN fg.goal_rank = fg.total_goals_in_group THEN fg.total_group_steps - CAST(FLOOR(fg.total_group_steps * (1.0 - fg.goal_share)) AS bigint) ELSE CAST(FLOOR(fg.total_group_steps * fg.goal_share) AS bigint) END,
                CASE WHEN fg.goal_rank = fg.total_goals_in_group THEN fg.total_group_coins - CAST(FLOOR(fg.total_group_coins * (1.0 - fg.goal_share)) AS bigint) ELSE CAST(FLOOR(fg.total_group_coins * fg.goal_share) AS bigint) END,
                CAST(fg.parent_pct AS int),
                fg.created_at
            FROM flattened_groups fg
            WHERE fg.goal_share > 0
        )
        -- 🔒 5. CLOSE CONTAINER QUEUE
        UPDATE contributions
        SET contribution_status_id = 5
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
    /**
     * Evaluates active entity campaigns safely isolated by primary key goal_instance_id.
     */
    @Modifying
    @Query(value = """
       UPDATE goal_instances gi
       SET
           steps_contributed = gi.steps_contributed + sub.total_steps,
           coins_contributed = gi.coins_contributed + sub.total_coins,
           dollar_amount     = gi.dollar_amount + sub.total_rev,
       
           -- SYSTEM EVALUATION TERMINATION LOGIC:
           is_complete   = CASE WHEN (gi.coins_contributed + sub.total_coins) >= gi.coins_needed THEN true ELSE gi.is_complete END,
           is_active     = CASE WHEN (gi.coins_contributed + sub.total_coins) >= gi.coins_needed THEN false ELSE gi.is_active END,
           completed_at  = CASE WHEN (gi.coins_contributed + sub.total_coins) >= gi.coins_needed AND gi.is_complete = false THEN NOW() ELSE gi.completed_at END,
       
           -- Link back to Catalog ID 7 (COMPLETED)
           pacing_status_id = CASE WHEN (gi.coins_contributed + sub.total_coins) >= gi.coins_needed THEN 7 ELSE gi.pacing_status_id END,
           end_date      = CASE WHEN (gi.coins_contributed + sub.total_coins) >= gi.coins_needed THEN CURRENT_DATE ELSE gi.end_date END,
           updated_at    = NOW()
       FROM (
           SELECT goal_instance_id, 
                  SUM(steps) as total_steps, 
                  SUM(coins) as total_coins, 
                  SUM(revenue_generated) as total_rev
           FROM entity_stats
           WHERE is_processed = false -- Reads only fresh data
           GROUP BY goal_instance_id
       ) sub
       WHERE gi.goal_instance_id = sub.goal_instance_id;
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
    /**
     * Evaluates active group campaigns safely isolated by primary key goal_instance_id.
     */
    @Modifying
    @Query(value = """
       UPDATE goal_instances gi
       SET
           steps_contributed = gi.steps_contributed + sub.total_steps,
           coins_contributed = gi.coins_contributed + sub.total_coins,
           is_complete       = CASE WHEN (gi.coins_contributed + sub.total_coins) >= gi.coins_needed THEN true ELSE gi.is_complete END,
           is_active         = CASE WHEN (gi.coins_contributed + sub.total_coins) >= gi.coins_needed THEN false ELSE gi.is_active END,
           completed_at      = CASE WHEN (gi.coins_contributed + sub.total_coins) >= gi.coins_needed AND gi.is_complete = false THEN NOW() ELSE gi.completed_at END,
           pacing_status_id  = CASE WHEN (gi.coins_contributed + sub.total_coins) >= gi.coins_needed THEN (SELECT pacing_status_id FROM public.pacing_statuses WHERE code = 'COMPLETED') ELSE gi.pacing_status_id END,
           end_date          = CASE WHEN (gi.coins_contributed + sub.total_coins) >= gi.coins_needed THEN CURRENT_DATE ELSE gi.end_date END,
           updated_at        = NOW()
       FROM (
           SELECT goal_instance_id, 
                  SUM(steps) as total_steps, 
                  SUM(coins) as total_coins
           FROM group_stats
           WHERE is_processed = false -- FIXED: Pulls fresh logs only
           GROUP BY goal_instance_id
       ) sub
       WHERE gi.goal_instance_id = sub.goal_instance_id;
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
   UPDATE public.goal_instances gi
   SET pacing_status_id = (
       SELECT pacing_status_id FROM public.pacing_statuses WHERE code = CASE
           WHEN NOW() > (gi.start_date + ((gi.end_date - gi.start_date) * INTERVAL '1 day' * 0.90)) AND gi.coins_contributed < (gi.coins_needed * 0.88) THEN 'NEAR_EXPIRATION_ALERT'
           WHEN NOW() > (gi.start_date + ((gi.end_date - gi.start_date) * INTERVAL '1 day' * 0.80)) AND gi.coins_contributed < (gi.coins_needed * 0.75) THEN 'HIGH_RISK_FAILURE'
           WHEN NOW() > (gi.start_date + ((gi.end_date - gi.start_date) * INTERVAL '1 day' * 0.75)) AND gi.coins_contributed < (gi.coins_needed * 0.65) THEN 'CRITICAL_LAGGING'
           WHEN NOW() > (gi.start_date + ((gi.end_date - gi.start_date) * INTERVAL '1 day' * 0.50)) AND gi.coins_contributed < (gi.coins_needed * 0.40) THEN 'BEHIND_NEED_RECALCULATION'
           WHEN NOW() > (gi.start_date + ((gi.end_date - gi.start_date) * INTERVAL '1 day' * 0.25)) AND gi.coins_contributed < (gi.coins_needed * 0.15) THEN 'SLIGHTLY_BEHIND'
           ELSE 'ON_TRACK'
       END
   ),
   updated_at = NOW() -- FIXED: Removed 'gi.' prefix to satisfy PostgreSQL strict SET syntax rules
   WHERE gi.is_active = true 
     -- Exclude finalized states from changing using the locked subquery mapping IDs
     AND gi.pacing_status_id NOT IN (
         SELECT pacing_status_id FROM public.pacing_statuses 
         WHERE code IN ('CLOSED_PENDING_AUDIT', 'SETTLED', 'COMPLETED')
     )
     -- IDEMPOTENCY SAFETY: Avoid blind writes by confirming the calculation actually changes the current state
     AND gi.pacing_status_id <> (
         SELECT pacing_status_id FROM public.pacing_statuses WHERE code = CASE
             WHEN NOW() > (gi.start_date + ((gi.end_date - gi.start_date) * INTERVAL '1 day' * 0.90)) AND gi.coins_contributed < (gi.coins_needed * 0.88) THEN 'NEAR_EXPIRATION_ALERT'
             WHEN NOW() > (gi.start_date + ((gi.end_date - gi.start_date) * INTERVAL '1 day' * 0.80)) AND gi.coins_contributed < (gi.coins_needed * 0.75) THEN 'HIGH_RISK_FAILURE'
             WHEN NOW() > (gi.start_date + ((gi.end_date - gi.start_date) * INTERVAL '1 day' * 0.75)) AND gi.coins_contributed < (gi.coins_needed * 0.65) THEN 'CRITICAL_LAGGING'
             WHEN NOW() > (gi.start_date + ((gi.end_date - gi.start_date) * INTERVAL '1 day' * 0.50)) AND gi.coins_contributed < (gi.coins_needed * 0.40) THEN 'BEHIND_NEED_RECALCULATION'
             WHEN NOW() > (gi.start_date + ((gi.end_date - gi.start_date) * INTERVAL '1 day' * 0.25)) AND gi.coins_contributed < (gi.coins_needed * 0.15) THEN 'SLIGHTLY_BEHIND'
             ELSE 'ON_TRACK'
         END
     )
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
   INSERT INTO public.user_achievements (user_id, achievement_id, goal_instance_id, earned_at)
   SELECT ug.user_id, g.achievement_id, gi.goal_instance_id, NOW()
   FROM public.user_goals ug
   JOIN public.goal_instances gi ON ug.goal_instance_id = gi.goal_instance_id
   JOIN public.goals g ON gi.goal_id = g.goal_id
   WHERE ug.is_complete = true 
     AND ug.user_goal_status_id = (SELECT user_goal_status_id FROM public.user_goal_statuses WHERE code = 'TRACKING')
   -- FIXED: Safe database-level upsert blocks duplicate writes across multiple records smoothly
   ON CONFLICT (user_id, goal_instance_id) DO NOTHING
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
        WHERE c.contribution_status_id = 5   -- allocated - was 2 for verified
          AND c.is_synced_to_user_goals = false -- 🛡️ Safe Queue: Never misses rows due to server downtime or midnight boundaries
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
            END,
            
            -- FIXED: Advance status code to METRICS_MET upon crossing target boundary thresholds
            user_goal_status_id = CASE 
                WHEN (ug.target_type = 'COINS' AND (ug.current_coins_progress + COALESCE(cp.daily_coins, 0)) >= ug.target_amount) OR
                     (ug.target_type = 'STEPS' AND (ug.current_steps_progress + COALESCE(cp.daily_steps, 0)) >= ug.target_amount)
                THEN (SELECT user_goal_status_id FROM public.user_goal_statuses WHERE code = 'METRICS_MET')
                ELSE ug.user_goal_status_id
            END
        FROM calculated_progress cp
        WHERE ug.user_id = cp.user_id 
          -- FIXED: Process ONLY records actively progressing down the tracking pipeline
          AND ug.user_goal_status_id = (SELECT user_goal_status_id FROM public.user_goal_statuses WHERE code = 'TRACKING')
          AND ug.is_active = true
        RETURNING ug.user_id, ug.is_complete, ug.goal_instance_id
    ),
    -- Mint a unique user achievement entry instantly for the completed row run
    inserted_achievements AS (
        INSERT INTO public.user_achievements (user_id, achievement_id, goal_instance_id, earned_at)
        SELECT uug.user_id, 101, uug.goal_instance_id, NOW() -- 101 = Standard Individual Goal Cleared Badge ID // TODO change this from 101
        FROM updated_user_goals uug
        WHERE uug.is_complete = true
        -- FIXED: Aligned targeting specifications with your new reliable index rule
        ON CONFLICT (user_id, goal_instance_id) DO NOTHING
        RETURNING user_id
    )
    -- 🔒 Close out the processed entries in the raw queue so they are never double counted
    UPDATE contributions
    SET is_synced_to_user_goals = true
    WHERE contribution_status_id = 5 
      AND is_synced_to_user_goals = false;
    """, nativeQuery = true)
    int evaluateUserGoalsBatch();


    /**
     * Retrieves all unique primary identifiers for goal instances that have reached
     * terminal completion conditions within the current processing timeframe.
     * <p>
     * Filters strictly for records marked as inactive (is_active = false) matching the
     * 'COMPLETED' pacing workflow status. This serves as the internal ingestion
     * vector for the automated bank settlement and payout dispatching module.
     *
     * @return a {@link List} of {@link Long} primary key tokens awaiting treasury release
     */
    /**
     * Retrieves unique primary identifiers for goal instances that have reached
     * terminal completion conditions within the current timeframe.
     */
    @Query(value = """
       SELECT goal_instance_id 
       FROM goal_instances 
       WHERE pacing_status = 'COMPLETED' 
         AND is_active = false
       """, nativeQuery = true)
    List<Long> findNewlyClosedGoalInstanceIds();

    @Modifying
    @Query(value = "UPDATE goal_instances SET pacing_status = :status WHERE goal_instance_id = :id", nativeQuery = true)
    void updatePacingStatus(@Param("id") Long id, @Param("status") String status);


    @Modifying
    @Query(value = "UPDATE public.entity_stats SET is_processed = true WHERE is_processed = false", nativeQuery = true)
    int clearProcessedEntityStatsQueue();

    @Modifying
    @Query(value = "UPDATE public.group_stats SET is_processed = true WHERE is_processed = false", nativeQuery = true)
    int clearProcessedGroupStatsQueue();


    @Modifying
    @Query(value = """
    UPDATE public.contributions 
    SET contribution_status_id = 3, -- Flips from ALLOCATED to PAID
        payout_batch_id = :payoutId
    WHERE entity_id = (SELECT entity_id FROM public.goal_instances WHERE goal_instance_id = :instanceId)
      AND contribution_status_id = 5 -- Targets only ALLOCATED metrics
    """, nativeQuery = true)
    void finalizeContributionsToPaid(@Param("instanceId") Long instanceId, @Param("payoutId") Long payoutId);

    /**
     * Updates a goal instance's catalog reference dynamically using its string code token.
     * Resolves the matching numeric primary key internally via an inline catalog table lookup.
     *
     * @param id the primary identifier of the target campaign goal instance
     * @param statusCode the text code identifier from the pacing catalog (e.g., 'CLOSED_PENDING_AUDIT', 'SETTLED')
     */
    @Modifying
    @Query(value = """
    UPDATE public.goal_instances 
    SET pacing_status_id = (SELECT pacing_status_id FROM public.pacing_statuses WHERE code = :statusCode)
    WHERE goal_instance_id = :id
    """, nativeQuery = true)
    void updatePacingStatusByCode(@Param("id") Long id, @Param("statusCode") String statusCode);




    // ======================================================================
    // 🛡️ RE-ALLOCATION COMPLIANCE API CHECKS
    // ======================================================================

    /**
     * Checks if an Entity Campaign's active instance is still open.
     * True means it's running; False means it's closed or missing.
     */
    @Query(value = """
        SELECT EXISTS (
            SELECT 1 FROM goal_instances 
            WHERE entity_id = :entityId 
              AND pacing_status_id NOT IN (SELECT pacing_status_id FROM public.pacing_statuses WHERE code = 'CLOSED_PENDING_AUDIT')
              AND end_date > CURRENT_TIMESTAMP
        )
        """, nativeQuery = true)
    boolean isEntityGoalActive(@Param("entityId") Long entityId);

    /**
     * Checks if a Friend Group Goal's active instance is still open.
     * True means it's running; False means it's closed or missing.
     */
    @Query(value = """
        SELECT EXISTS (
            SELECT 1 FROM goal_instances 
            WHERE group_id = :groupId 
              AND pacing_status_id NOT IN (SELECT pacing_status_id FROM public.pacing_statuses WHERE code = 'CLOSED_PENDING_AUDIT')
              AND end_date > CURRENT_TIMESTAMP
        )
        """, nativeQuery = true)
    boolean isGroupGoalActive(@Param("groupId") Long groupId);

    @Query(value = "SELECT name FROM entities WHERE entity_id = :entityId", nativeQuery = true)
    String findEntityNameById(@Param("entityId") Long entityId);

    @Query(value = "SELECT name FROM groups WHERE group_id = :groupId", nativeQuery = true)
    String findGroupNameById(@Param("groupId") Long groupId);

}


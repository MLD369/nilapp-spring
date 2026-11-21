package com.mldtech.nilapp.api.batches.service;

import com.mldtech.nilapp.api.UserStreak.model.UserStreak;
import com.mldtech.nilapp.api.UserStreak.repository.UserStreakRepository;
import com.mldtech.nilapp.api.contributions.repository.ContributionRepository;
import com.mldtech.nilapp.api.daily_stat.repository.DailyStatRepository;
import com.mldtech.nilapp.api.GoalInstances.model.GoalInstance;
import com.mldtech.nilapp.api.GoalInstances.repository.GoalInstanceRepository;
import com.mldtech.nilapp.api.payout_batches.service.PayoutService;
import com.mldtech.nilapp.api.UserGoal.model.UserGoal;
import com.mldtech.nilapp.api.UserGoal.repository.UserGoalRepository;
import com.mldtech.nilapp.enums.PacingStatusEnum;
import com.mldtech.nilapp.enums.UserGoalStatusEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static com.mldtech.nilapp.helper.AppConstants.AMOUNT_FOR_STREAK;
import static com.mldtech.nilapp.helper.AppConstants.SHIELD_MILESTONES;

@Service
public class BatchService {

    private static final Logger log = LoggerFactory.getLogger(BatchService.class);

    private final ContributionRepository contributionRepository;
    private final DailyStatRepository dailyStatRepository;
    private final GoalInstanceRepository goalInstanceRepository;
    private final PayoutService payoutService;
    private final UserGoalRepository userGoalRepository;
    private final UserStreakRepository userStreakRepository;


    // ======================================================================
    // Increase this value (e.g., to 5, 7, 14) as your user base scales to millions
    // to give manual audit teams and fraud verification queues more time to clear.
    private static final int WORKOUT_COOLING_DAYS = 0; // TODO will need to find a good date for these and use app constants

    public BatchService(ContributionRepository contributionRepository, PayoutService payoutService, UserGoalRepository userGoalRepository, DailyStatRepository dailyStatRepository, GoalInstanceRepository goalInstanceRepository, UserStreakRepository userStreakRepository) {
        this.contributionRepository = contributionRepository;
        this.payoutService = payoutService;
        this.userGoalRepository = userGoalRepository;
        this.dailyStatRepository = dailyStatRepository;
        this.goalInstanceRepository = goalInstanceRepository;
        this.userStreakRepository = userStreakRepository;
    }

    // Inject PayoutService alongside your repository at the class level:
// private final PayoutService payoutService;

    /**
     * Master orchestration metric processing engine. Runs every six hours to clear ingestion
     * layers, update goal completions, verify timelines, and issue automated corporate distributions.
     *
     * @return count of pending raw contributions processed
     */
    @Transactional
    public int executeContributionsAllocationBatch() {
        log.info("Starting master metrics evaluation and financial allocation batch pipeline...");

        try {
            // 1. Flatten Ingestion Layers (Calculates Revenue & Retained Profit allocations)
            int rowsUpdated = contributionRepository.processPendingContributions();

            // 2. Evaluate Entity Campaigns (Updates progress vectors via completed ingestion status maps)
            int entityGoals = contributionRepository.evaluateEntityGoals();

            // 3. Evaluate Group Campaigns (Tracks social metrics)
            int groupGoals = contributionRepository.evaluateGroupGoals();
            // Flips flags to true so these data snapshots are never re-evaluated
            contributionRepository.clearProcessedEntityStatsQueue();
            contributionRepository.clearProcessedGroupStatsQueue();

            // 4. Calibrate Pacing Trackers (Updates status strings for running instances)
            int pacingUpdates = contributionRepository.recalculateGoalPacingStatuses();

            // 5. Sync member contracts from updated parent instances
            int userGoalsFinalized = contributionRepository.finalizeUserGoalsFromInstances();

            // 6. Process individual achievements and badges
            int individualUserGoalsProcessed = contributionRepository.evaluateUserGoalsBatch();
            int achievementsEarned = contributionRepository.assignUserAchievements();

            // ======================================================================
            // 7. AUTOMATED FINANCIAL DISCOVERY ENGINE (CREATES DRAFTS / CAPTURES FAILURES)
            // ======================================================================
            int automatedPayoutsDiscovered = 0;

            // This lookup returns ONLY instances currently flagged as 'COMPLETED'
            List<Long> completedInstanceIds = contributionRepository.findNewlyClosedGoalInstanceIds();

            for (Long instanceId : completedInstanceIds) {
                try {
                    if (rowsUpdated > 0) { // TODO needs to be commented out for mocks
                        // Stage A: Advance campaign state to CLOSED_PENDING_AUDIT using string catalog lookup
                        contributionRepository.updatePacingStatusByCode(instanceId, PacingStatusEnum.CLOSED_PENDING_AUDIT.name());
                        List<Long> childUserGoalIds = userGoalRepository.findUserGoalIdsByInstanceId(instanceId);
                        for (Long userGoalId : childUserGoalIds) {
                            userGoalRepository.updateUserGoalStatusByCode(userGoalId, UserGoalStatusEnum.ARCHIVED_SUCCESS.name());
                        }


                        // Stage B: Initialize the record in its default baseline state
                        payoutService.createInitialPayoutDraft(instanceId);
                        automatedPayoutsDiscovered++;
                    }
                } catch (Exception e) {
                    log.error("Failed to process payout discovery for Goal Instance ID: {}. Recording system failure log...", instanceId, e);
                    try {
                        // Internal fallback: Write a logging record flagged as FAILED to inform admins
                        payoutService.recordProcessingFailure(instanceId, e.getMessage());
                    } catch (Exception fatal) {
                        log.error("Fatal failure block write suppression: {}", fatal.getMessage());
                    }
                }
            }


            log.info("""
                
                ======================================================================
                📈 REVENUE-ALIGNED BATCH PIPELINE SUCCESS SUMMARY (LOCAL CONSOLE AUDIT)
                ======================================================================
                🔹 Pending Ad Claims Approved (contributions)  -> [ {} records processed ]
                🔹 Entity Financial Goals (entity_stats)       -> [ {} goals updated ]
                🔹 Group Social Goals (group_stats)            -> [ {} goals evaluated ]
                🔹 Campaign/Entity Pacing (goal_instances)     -> [ {} statuses calibrated ]
                🔹 Team Goals Closed & Synced (user_goals)     -> [ {} user_goals closed ]
                🔹 Individual User Goals Closed (user_goals)   -> [ {} goals closed & archived ]
                🔹 Accolades & Badges Minted (user_achievements)-> [ {} achievements earned ]
                🔹 Automated Treasury Payouts Issued          -> [ {} batches settled ]
                ======================================================================
                """,
                    rowsUpdated, entityGoals, groupGoals, pacingUpdates,
                    userGoalsFinalized, individualUserGoalsProcessed, achievementsEarned,
                    automatedPayoutsDiscovered
            );

            return rowsUpdated;

        } catch (Exception e) {
            log.error("Batch processing pipeline failed. All structural and financial changes rolled back automatically.", e);
            throw e;
        }
    }
    /**
     * Workout Metrics Delta Engine.
     * Sweeps up all newly ALLOCATED contributions into daily logs and streams any new
     * incremental increases directly into user lifetime summaries in real-time.
     */
    @Transactional
    public void executeDailyStatsAggregationBatch() {
        log.info("Starting automated workout metrics delta rollup pipeline...");

        int dailyStatsCreatedOrUpdated = 0;
        int contributionsAllocated = 0;
        int profileMetricsUpdated = 0;
        int dailyStatsClosed = 0;

        try {
            // Pass 1: Continually roll up newly ALLOCATED items into their specific calendar dates
            dailyStatsCreatedOrUpdated = dailyStatRepository.aggregateAllocatedContributionsToDailyStats();

            // Pass 2: Clear processed items out of the raw contribution entry queue
            contributionsAllocated = dailyStatRepository.clearProcessedContributionsQueue();

            // ✅ Pass 3A: Streams deltas safely to profiles and returns the true number of users modified
            profileMetricsUpdated = dailyStatRepository.streamWorkoutDeltasToUserLifetimes();

            // ✅ Pass 3B: Flushes progress markers and returns the true number of daily stat records closed
            dailyStatsClosed = dailyStatRepository.clearProcessedDailyStatsQueue();

            log.info("""
                
                ======================================================================
                🏃‍♂️ DECOUPLED WORKOUT LOGISTICS PIPELINE SUCCESS SUMMARY
                ======================================================================
                🔹 Contributions Processed                    -> [ {} claims ingested ]
                🔹 Daily Stat Records Affected                -> [ {} log rows updated ]
                🔹 Lifetime Profiles Updated (users)          -> [ {} accounts modified ]
                🔹 Sync Transactions Finalized                -> [ {} records finalized ]
                ======================================================================
                """,
                    contributionsAllocated,
                    dailyStatsCreatedOrUpdated,
                    profileMetricsUpdated,
                    dailyStatsClosed
            );

        } catch (Exception e) {
            log.error("Workout log sync pipeline failed. Atomic database rollbacks applied.", e);
            throw e;
        }
    }


    /**
     * Closes expired goal instances and cascades closure to active user goals.
     * Triggered nightly at 12:05 AM by BatchScheduler.
     */
    @Transactional
    public void closeExpiredGoals() {
        LocalDate today = LocalDate.now();
        log.info("Starting daily goal expiration batch for date: {}", today);

        // 1. Close expired GoalInstances
        List<GoalInstance> expiredInstances = goalInstanceRepository.findExpiredActiveInstances(today);
        for (GoalInstance gi : expiredInstances) {
            gi.setIsActive(false);
        }
        if (!expiredInstances.isEmpty()) {
            goalInstanceRepository.saveAll(expiredInstances);
        }

        // 2. Cascade closure to any active UserGoals tied to expired or inactive GoalInstances
        List<UserGoal> activeUserGoalsToClose = userGoalRepository.findOrphanedOrExpiredActiveUserGoals(today);
        LocalDateTime now = LocalDateTime.now();

        for (UserGoal ug : activeUserGoalsToClose) {
            ug.setIsActive(false);
            if (ug.getLeftAt() == null && !Boolean.TRUE.equals(ug.getIsComplete())) {
                ug.setLeftAt(now);
            }
        }
        if (!activeUserGoalsToClose.isEmpty()) {
            userGoalRepository.saveAll(activeUserGoalsToClose);
        }

        log.info("Goal expiration batch complete. Closed {} GoalInstances and {} UserGoals.",
                expiredInstances.size(), activeUserGoalsToClose.size());
    }



    /**
     * Freezes eligible user_streaks rows (FOR UPDATE), increments clean_streak_days,
     * and unlocks shieldClaimable = true on exact consistency milestones (3, 7, 14, 30, 60, 100, 200...).
     * Triggered nightly at 12:10 AM by BatchScheduler.
     */
    @Transactional
    public void executeNightlyShieldMilestoneBatch() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        log.info("Starting nightly streak shield unlock batch for date: {}", yesterday);

        int rowsFrozenAndEvaluated = 0;
        int cleanStreaksIncremented = 0;
        int shieldsUnlockedForClaim = 0;

        try {
            List<UserStreak> lockedProfiles = userStreakRepository.findAndLockProfilesEligibleForShieldEval(yesterday);
            rowsFrozenAndEvaluated = lockedProfiles.size();

            for (UserStreak streakRecord : lockedProfiles) {
                Long userId = streakRecord.getUser().getUserId();
                // TODO update to user the method long countYesterdayTotalClaimsAttempts(
                //            @Param("userId") Long userId,
                //            @Param("timezone") String timezone,
                //            @Param("yesterday") LocalDate yesterday
                //    );
                long yesterdayClaims = userStreakRepository.countYesterdayTotalClaimsAttempts(userId);

                if (yesterdayClaims >= AMOUNT_FOR_STREAK) {
                    int newCleanDays = (streakRecord.getCleanStreakDays() != null ? streakRecord.getCleanStreakDays() : 0) + 1;
                    streakRecord.setCleanStreakDays(newCleanDays);
                    cleanStreaksIncremented++;

                    boolean isMilestone = SHIELD_MILESTONES.contains(newCleanDays)
                            || (newCleanDays > 100 && newCleanDays % 100 == 0);

                    if (isMilestone) {
                        streakRecord.setShieldClaimable(true);
                        shieldsUnlockedForClaim++;
                        log.info("User ID {} unlocked a claimable Streak Shield at {} clean days!", userId, newCleanDays);
                    }
                } else {
                    // Yesterday was rescued via shield/saver instead of normal claims -> reset clean streak
                    streakRecord.setCleanStreakDays(0);
                }

                streakRecord.setLastShieldEvaluatedDate(yesterday);
                streakRecord.setUpdatedAt(LocalDateTime.now());
            }

            if (!lockedProfiles.isEmpty()) {
                userStreakRepository.saveAll(lockedProfiles);
            }

            log.info("""
                
                ======================================================================
                🛡️ NIGHTLY STREAK SHIELD UNLOCK BATCH SUMMARY (DATE: {})
                ======================================================================
                🔹 User Streak Rows Frozen & Evaluated        -> [ {} rows locked ]
                🔹 Clean Streak Days Incremented              -> [ {} clean days updated ]
                🔹 Shields Unlocked for UI Claim              -> [ {} shields unlocked ]
                ======================================================================
                """,
                    yesterday,
                    rowsFrozenAndEvaluated,
                    cleanStreaksIncremented,
                    shieldsUnlockedForClaim
            );

        } catch (Exception e) {
            log.error("Nightly streak shield batch failed. Row locks released and changes rolled back.", e);
            throw e;
        }
    }

}
/*

API to allocate claims to groups and entities - done
change contributions - done

	Api to check entity goals - done
	Api to check group goals - done
	Api to assign achievements - done


Todo	Notification that goal or achievement reached (ui maybe when user opens app or push notificatookmn)


    Api to save total steps - once a day when user opens app - done - TODO ui
	Api to save user streak - once a day when user opens app - done - todo ui
	shield /streak saver
  */
/*

/* TODO newest
-- ======================================================================
-- STEP 1: INITIAL CLEANUP (Teardown previous test cycles cleanly)
-- ======================================================================
-- 1. BREAK CIRCULAR FK DEPENDENCY: Null out the payout batch pointers inside contributions first
UPDATE public.contributions
SET payout_batch_id = NULL
WHERE user_id = 1 OR payout_batch_id IN (
    SELECT payout_batch_id FROM public.payout_batches
    WHERE goal_instance_id IN (999011, 999012, 999013, 999014, 999015, 999016, 999017, 999001, 999002, 999003, 999004)
);

-- 2. NOW SAFE TO WIPE: Wipe children records generated by automated payouts
DELETE FROM public.payout_batches WHERE goal_instance_id IN (999011, 999012, 999013, 999014, 999015, 999016, 999017, 999001, 999002, 999003, 999004);

-- 3. Clean child join tables and metrics
DELETE FROM public.user_achievements WHERE achievement_id IN (101, 999901, 999902) OR user_id = 1;
DELETE FROM public.entity_stats WHERE user_id = 1;
DELETE FROM public.group_stats WHERE user_id = 1;
DELETE FROM public.contributions WHERE user_id = 1;

-- 4. Clean core testing tracking structures
DELETE FROM public.user_goals WHERE user_goal_id IN (999801, 999802, 999803);
DELETE FROM public.goal_instances WHERE goal_instance_id IN (999011, 999012, 999013, 999014, 999015, 999016, 999017, 999001, 999002, 999003, 999004);
DELETE FROM public.goals WHERE goal_id = 999;
DELETE FROM public.achievements WHERE achievement_id IN (101, 999901, 999902);
DELETE FROM public.user_achievements WHERE goal_instance_id IN (999011, 999012, 999013, 999014, 999015, 999016, 999017, 999001, 999002, 999003, 999004) OR user_id = 1;


-- ======================================================================
-- STEP 2: BASE SYSTEM CATALOG DATA (Includes critical Java Target ID 101)
-- ======================================================================
INSERT INTO public.achievements (achievement_id, achievement_type, achievement, description, badge, required_steps, required_distance, required_coins)
VALUES
(101, 1, 'TEST_INDIVIDUAL_GOAL_BADGE', 'Real ID expected by Java pipeline assignment', 'badge_101.png', 0, 0, 0),
(999901, 1, 'TEST_USER_MILESTONE_BADGE', 'Obvious individual milestone badge', 'badge_test_user.png', 0, 0, 0),
(999902, 2, 'TEST_COMMUNAL_GOAL_BADGE', 'Obvious communal goal closure badge', 'badge_test_communal.png', 0, 0, 0)
ON CONFLICT (achievement_id) DO NOTHING;

INSERT INTO public.goals (goal_id, goal_types_id, achievement_id, goal, description, completed_date, goal_lock_date, metric_type)
VALUES
(999, 1, 101, 'TEST_SYSTEM_TEMPLATE_GOAL', 'Obvious master baseline blueprint task', NULL, NULL, 'COINS')
ON CONFLICT (goal_id) DO NOTHING;


-- ======================================================================
-- STEP 3: MOCK RAW INGESTION PIPELINE DATA
-- ======================================================================
INSERT INTO public.contributions (
    contribution_id, user_id, entity_id, campaign_id, steps_contributed, coins_contributed,
    conversion_rate, ad_session_id, created_at, payout_batch_id, contribution_status_id,
    allocation_snapshot, contribution_type_id
)
VALUES (
    999501, 1, 1, NULL, 100000, 1000,
    0.10, 999999, NOW(), NULL, 1,
    '{"entityAllocations": [{"entityId": 1, "percentage": 100}], "groupAllocations": [{"groupId": 1, "percentage": 100}]}'::jsonb, 1
);

INSERT INTO public.contributions (
    contribution_id, user_id, entity_id, campaign_id, steps_contributed, coins_contributed,
    conversion_rate, ad_session_id, created_at, payout_batch_id, contribution_status_id,
    allocation_snapshot, contribution_type_id
)
VALUES (
    999501, 1, 1, NULL, 1, 1,
    0.10, 999999, NOW(), NULL, 1,
    '{"entityAllocations": [{"entityId": 1, "percentage": 100}], "groupAllocations": [{"groupId": 1, "percentage": 100}]}'::jsonb, 1
);


-- ======================================================================
-- STEP 4: SECTION A - DYNAMIC PACING TEST CONFIGURATIONS (Cases 1-7)
-- ======================================================================
INSERT INTO public.goal_instances (goal_instance_id, goal_id, entity_id, group_id, start_date, end_date, coins_contributed, coins_needed, steps_contributed, steps_needed, dollar_amount, is_active, is_complete, pacing_status, created_at, updated_at)
VALUES
(999011, 999, 1, NULL, CURRENT_DATE - INTERVAL '91 days', CURRENT_DATE + INTERVAL '9 days', 100, 1000, 10000, 100000, 50.00, true, false, 'ON_TRACK', NOW(), NOW()),
(999012, 999, 1, NULL, CURRENT_DATE - INTERVAL '81 days', CURRENT_DATE + INTERVAL '19 days', 700, 1000, 70000, 100000, 350.00, true, false, 'ON_TRACK', NOW(), NOW()),
(999013, 999, 1, NULL, CURRENT_DATE - INTERVAL '76 days', CURRENT_DATE + INTERVAL '24 days', 600, 1000, 60000, 100000, 300.00, true, false, 'ON_TRACK', NOW(), NOW()),
(999014, 999, 1, NULL, CURRENT_DATE - INTERVAL '52 days', CURRENT_DATE + INTERVAL '48 days', 350, 1000, 35000, 100000, 175.00, true, false, 'ON_TRACK', NOW(), NOW()),
(999015, 999, 1, NULL, CURRENT_DATE - INTERVAL '26 days', CURRENT_DATE + INTERVAL '74 days', 100, 1000, 10000, 100000, 50.00, true, false, 'ON_TRACK', NOW(), NOW()),
(999016, 999, 1, NULL, CURRENT_DATE - INTERVAL '50 days', CURRENT_DATE + INTERVAL '50 days', 550, 1000, 55000, 100000, 275.00, true, false, 'ON_TRACK', NOW(), NOW()),
(999017, 999, 1, NULL, CURRENT_DATE - INTERVAL '24 days', CURRENT_DATE + INTERVAL '76 days', 50, 1000, 5000, 100000, 25.00, true, false, 'ON_TRACK', NOW(), NOW());


-- ======================================================================
-- STEP 5: SECTION B - IMMEDIATE CLOSURES & ACCOLADE MINTING (Cases 8-10)
-- ======================================================================
INSERT INTO public.goal_instances (goal_instance_id, goal_id, entity_id, group_id, start_date, end_date, current_progress, is_active, is_complete, steps_contributed, coins_contributed, steps_needed, coins_needed, dollar_amount, completed_at, pacing_status, created_at, updated_at)
VALUES (999001, 999, 1, NULL, CURRENT_DATE - INTERVAL '10 days', CURRENT_DATE + INTERVAL '20 days', 1000, true, false, 100000, 1000, 100000, 1000, 500.00, NULL, 'ON_TRACK', NOW(), NOW());

INSERT INTO public.user_goals (user_goal_id, user_id, goal_instance_id, allocation_pct, joined_at, left_at, is_active, is_complete, completed_at, target_type, target_amount, current_steps_progress, current_coins_progress, tier_level)
VALUES (999801, 1, 999001, 100.00, NOW() - INTERVAL '5 days', NULL, true, false, NULL, 'COINS', 1000, 50000, 550, 1);

INSERT INTO public.goal_instances (goal_instance_id, goal_id, entity_id, group_id, start_date, end_date, current_progress, is_active, is_complete, steps_contributed, coins_contributed, steps_needed, coins_needed, dollar_amount, completed_at, pacing_status, created_at, updated_at)
VALUES (999002, 999, NULL, 1, CURRENT_DATE - INTERVAL '5 days', CURRENT_DATE + INTERVAL '25 days', 2000, true, false, 200000, 2000, 200000, 2000, 0.00, NULL, 'ON_TRACK', NOW(), NOW());

INSERT INTO public.user_goals (user_goal_id, user_id, goal_instance_id, allocation_pct, joined_at, left_at, is_active, is_complete, completed_at, target_type, target_amount, current_steps_progress, current_coins_progress, tier_level)
VALUES (999802, 1, 999002, 100.00, NOW() - INTERVAL '3 days', NULL, true, false, NULL, 'COINS', 2000, 20000, 300, 1);

INSERT INTO public.goal_instances (goal_instance_id, goal_id, entity_id, group_id, start_date, end_date, current_progress, is_active, is_complete, steps_contributed, coins_contributed, steps_needed, coins_needed, dollar_amount, completed_at, pacing_status, created_at, updated_at)
VALUES (999003, 999, 1, NULL, CURRENT_DATE - INTERVAL '1 day', CURRENT_DATE + INTERVAL '29 days', 550, true, false, 0, 550, 0, 500, 0.00, NULL, 'ON_TRACK', NOW(), NOW());

INSERT INTO public.user_goals (user_goal_id, user_id, goal_instance_id, allocation_pct, joined_at, left_at, is_active, is_complete, completed_at, target_type, target_amount, current_steps_progress, current_coins_progress, tier_level)
VALUES (999803, 1, 999003, 0.00, NOW() - INTERVAL '1 day', NULL, true, false, NULL, 'COINS', 500, 0, 550, 1);


-- ======================================================================
-- STEP 6: SEED TARGET FOR THE AUTOMATED PAYOUT ENGINE LOOP
-- ======================================================================
INSERT INTO public.goal_instances (
    goal_instance_id, goal_id, entity_id, group_id, start_date, end_date,
    current_progress, is_active, is_complete, steps_contributed, coins_contributed,
    steps_needed, coins_needed, dollar_amount, completed_at, pacing_status, created_at, updated_at
)
VALUES (
    999004, 999, 1, NULL, CURRENT_DATE - INTERVAL '10 days', CURRENT_DATE - INTERVAL '1 day',
    500, false, true, 0, 500, 0, 500, 150.00, NOW(), 'COMPLETED', NOW(), NOW()
);



 */
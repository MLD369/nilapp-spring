package com.mldtech.nilapp.api.batches.service;

import com.mldtech.nilapp.api.contributions.repository.ContributionRepository;
import com.mldtech.nilapp.api.payout_batches.service.PayoutService;
import com.mldtech.nilapp.api.users.children.UserGoal.repository.UserGoalRepository;
import com.mldtech.nilapp.enums.PacingStatusEnum;
import com.mldtech.nilapp.enums.UserGoalStatusEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BatchService {

    private static final Logger log = LoggerFactory.getLogger(BatchService.class);
    private final ContributionRepository contributionRepository;
    private final PayoutService payoutService;
    private final UserGoalRepository userGoalRepository;

    public BatchService(ContributionRepository contributionRepository, PayoutService payoutService, UserGoalRepository userGoalRepository) {
        this.contributionRepository = contributionRepository;
        this.payoutService = payoutService;
        this.userGoalRepository = userGoalRepository;
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
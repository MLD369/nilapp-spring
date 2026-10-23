package com.mldtech.nilapp.api.batches.service;

import com.mldtech.nilapp.api.contributions.repository.ContributionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BatchService {

    private static final Logger log = LoggerFactory.getLogger(BatchService.class);
    private final ContributionRepository contributionRepository;

    public BatchService(ContributionRepository contributionRepository) {
        this.contributionRepository = contributionRepository;
    }

    @Transactional
    public int executeContributionsAllocationBatch() {
        log.info("Starting master metrics evaluation and financial allocation batch pipeline...");

        try {
            // 1. Process and Flatten Ingestion Layers (Calculates Entity Revenue & Platform Profit)
            int rowsUpdated = contributionRepository.processPendingContributions();

            // 2. Evaluate Entity Campaigns (Updates coins, steps, real dollar totals, and handles status transformations)
            int entityGoals = contributionRepository.evaluateEntityGoals();

            // 3. Evaluate Group Campaigns (Pure gamification - tracks steps and coins ONLY)
            int groupGoals = contributionRepository.evaluateGroupGoals();

            // 4. Update pacing status indicators across all active initiatives
            int pacingUpdates = contributionRepository.recalculateGoalPacingStatuses();

            // 5. Sync individual contract rows right after parent instances update
            int userGoalsFinalized = contributionRepository.finalizeUserGoalsFromInstances();

            // 6. Evaluate individual user-driven goals if master records were modified
            int individualUserGoalsProcessed = 0;
            int achievementsEarned = 0;

            if (rowsUpdated > 0) { // TODO needs to be commented out for mocks
                individualUserGoalsProcessed = contributionRepository.evaluateUserGoalsBatch();
                achievementsEarned = contributionRepository.assignUserAchievements();
            }

            // Consolidated summary analytics log block prevents raw SQL lines from cluttering output
            // Consolidated summary analytics log block tells you exactly what closed or updated

            // Detailed diagnostic output featuring PostgreSQL table origins explicitly
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
                ======================================================================
                """,
                    rowsUpdated, entityGoals, groupGoals, pacingUpdates,
                    userGoalsFinalized, individualUserGoalsProcessed, achievementsEarned
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
TODO code to test above method
-- ======================================================================
-- STEP 1: INITIAL CLEANUP (Teardown previous test cycles)
-- ======================================================================
DELETE FROM public.payout_batches WHERE goal_instance_id IN (999011, 999012, 999013, 999014, 999015, 999016, 999017, 999001, 999002, 999003);
DELETE FROM public.user_goals WHERE user_goal_id IN (999801, 999802, 999803);
DELETE FROM public.goal_instances WHERE goal_instance_id IN (999011, 999012, 999013, 999014, 999015, 999016, 999017, 999001, 999002, 999003);
DELETE FROM public.goals WHERE goal_id = 999;
DELETE FROM public.achievements WHERE achievement_id IN (999901, 999902);


-- ======================================================================
-- STEP 2: BASE SYSTEM CATALOG DATA
-- ======================================================================
INSERT INTO public.achievements (achievement_id, achievement_type, achievement, description, badge, required_steps, required_distance, required_coins)
VALUES
(999901, 1, 'TEST_USER_MILESTONE_BADGE', 'Obvious individual milestone badge', 'badge_test_user.png', 0, 0, 0),
(999902, 2, 'TEST_COMMUNAL_GOAL_BADGE', 'Obvious communal goal closure badge', 'badge_test_communal.png', 0, 0, 0)
ON CONFLICT (achievement_id) DO NOTHING;

-- Creates a test goal blueprint using your exact Goal Type 1
INSERT INTO public.goals (goal_id, goal_types_id, achievement_id, goal, description, completed_date, goal_lock_date, metric_type)
VALUES
(999, 1, 999902, 'TEST_SYSTEM_TEMPLATE_GOAL', 'Obvious master baseline blueprint task', NULL, NULL, 'COINS')
ON CONFLICT (goal_id) DO NOTHING;


-- ======================================================================
-- STEP 3: SECTION A - DYNAMIC PACING TEST CONFIGURATIONS (Cases 1-7)
-- ======================================================================

-- 1. EXPECTS: 'NEAR_EXPIRATION_ALERT' (91% time, 10% coins)
INSERT INTO public.goal_instances (goal_instance_id, goal_id, entity_id, group_id, start_date, end_date, coins_contributed, coins_needed, steps_contributed, steps_needed, dollar_amount, is_active, is_complete, pacing_status, created_at, updated_at)
VALUES (999011, 999, 1, NULL, CURRENT_DATE - INTERVAL '91 days', CURRENT_DATE + INTERVAL '9 days', 100, 1000, 10000, 100000, 50.00, true, false, 'ON_TRACK', NOW(), NOW());

-- 2. EXPECTS: 'HIGH_RISK_FAILURE' (81% time, 70% coins)
INSERT INTO public.goal_instances (goal_instance_id, goal_id, entity_id, group_id, start_date, end_date, coins_contributed, coins_needed, steps_contributed, steps_needed, dollar_amount, is_active, is_complete, pacing_status, created_at, updated_at)
VALUES (999012, 999, 1, NULL, CURRENT_DATE - INTERVAL '81 days', CURRENT_DATE + INTERVAL '19 days', 700, 1000, 70000, 100000, 350.00, true, false, 'ON_TRACK', NOW(), NOW());

-- 3. EXPECTS: 'CRITICAL_LAGGING' (76% time, 60% coins)
INSERT INTO public.goal_instances (goal_instance_id, goal_id, entity_id, group_id, start_date, end_date, coins_contributed, coins_needed, steps_contributed, steps_needed, dollar_amount, is_active, is_complete, pacing_status, created_at, updated_at)
VALUES (999013, 999, 1, NULL, CURRENT_DATE - INTERVAL '76 days', CURRENT_DATE + INTERVAL '24 days', 600, 1000, 60000, 100000, 300.00, true, false, 'ON_TRACK', NOW(), NOW());

-- 4. EXPECTS: 'BEHIND_NEED_RECALCULATION' (52% time, 35% coins)
INSERT INTO public.goal_instances (goal_instance_id, goal_id, entity_id, group_id, start_date, end_date, coins_contributed, coins_needed, steps_contributed, steps_needed, dollar_amount, is_active, is_complete, pacing_status, created_at, updated_at)
VALUES (999014, 999, 1, NULL, CURRENT_DATE - INTERVAL '52 days', CURRENT_DATE + INTERVAL '48 days', 350, 1000, 35000, 100000, 175.00, true, false, 'ON_TRACK', NOW(), NOW());

-- 5. EXPECTS: 'SLIGHTLY_BEHIND' (26% time, 10% coins)
INSERT INTO public.goal_instances (goal_instance_id, goal_id, entity_id, group_id, start_date, end_date, coins_contributed, coins_needed, steps_contributed, steps_needed, dollar_amount, is_active, is_complete, pacing_status, created_at, updated_at)
VALUES (999015, 999, 1, NULL, CURRENT_DATE - INTERVAL '26 days', CURRENT_DATE + INTERVAL '74 days', 100, 1000, 10000, 100000, 50.00, true, false, 'ON_TRACK', NOW(), NOW());

-- 6. EXPECTS: 'ON_TRACK' (50% time, 55% coins)
INSERT INTO public.goal_instances (goal_instance_id, goal_id, entity_id, group_id, start_date, end_date, coins_contributed, coins_needed, steps_contributed, steps_needed, dollar_amount, is_active, is_complete, pacing_status, created_at, updated_at)
VALUES (999016, 999, 1, NULL, CURRENT_DATE - INTERVAL '50 days', CURRENT_DATE + INTERVAL '50 days', 550, 1000, 55000, 100000, 275.00, true, false, 'ON_TRACK', NOW(), NOW());

-- 7. EXPECTS: 'ON_TRACK' (24% time)
INSERT INTO public.goal_instances (goal_instance_id, goal_id, entity_id, group_id, start_date, end_date, coins_contributed, coins_needed, steps_contributed, steps_needed, dollar_amount, is_active, is_complete, pacing_status, created_at, updated_at)
VALUES (999017, 999, 1, NULL, CURRENT_DATE - INTERVAL '24 days', CURRENT_DATE + INTERVAL '76 days', 50, 1000, 5000, 100000, 25.00, true, false, 'ON_TRACK', NOW(), NOW());


-- ======================================================================
-- STEP 4: SECTION B - IMMEDIATE CLOSURES & ACCOLADE MINTING (Cases 8-10)
-- ======================================================================

-- 8. EXPECTS IMMEDIATE ENTITY GOAL CLOSURE, SYNCHRONIZED TEAM RESET, & PAYOUT DRAFT
INSERT INTO public.goal_instances (goal_instance_id, goal_id, entity_id, group_id, start_date, end_date, current_progress, is_active, is_complete, steps_contributed, coins_contributed, steps_needed, coins_needed, dollar_amount, completed_at, pacing_status, created_at, updated_at)
VALUES (999001, 999, 1, NULL, CURRENT_DATE - INTERVAL '10 days', CURRENT_DATE + INTERVAL '20 days', 1000, true, false, 100000, 1000, 100000, 1000, 500.00, NULL, 'ON_TRACK', NOW(), NOW());

INSERT INTO public.user_goals (user_goal_id, user_id, goal_instance_id, allocation_pct, joined_at, left_at, is_active, is_complete, completed_at, target_type, target_amount, current_steps_progress, current_coins_progress, tier_level)
VALUES (999801, 1, 999001, 100.00, NOW() - INTERVAL '5 days', NULL, true, false, NULL, 'COINS', 1000, 50000, 550, 1);


-- 9. EXPECTS IMMEDIATE GROUP GOAL CLOSURE & SOCIAL ACHIEVEMENT DISTRIBUTION
INSERT INTO public.goal_instances (goal_instance_id, goal_id, entity_id, group_id, start_date, end_date, current_progress, is_active, is_complete, steps_contributed, coins_contributed, steps_needed, coins_needed, dollar_amount, completed_at, pacing_status, created_at, updated_at)
VALUES (999002, 999, NULL, 1, CURRENT_DATE - INTERVAL '5 days', CURRENT_DATE + INTERVAL '25 days', 2000, true, false, 200000, 2000, 200000, 2000, 0.00, NULL, 'ON_TRACK', NOW(), NOW());

INSERT INTO public.user_goals (user_goal_id, user_id, goal_instance_id, allocation_pct, joined_at, left_at, is_active, is_complete, completed_at, target_type, target_amount, current_steps_progress, current_coins_progress, tier_level)
VALUES (999802, 1, 999002, 100.00, NOW() - INTERVAL '3 days', NULL, true, false, NULL, 'COINS', 2000, 20000, 300, 1);


-- 10. EXPECTS IMMEDIATE INDIVIDUAL SOLO GOAL CLOSURE & BADGE MINTING
-- Create individual instance record to pass schema constraint validation
INSERT INTO public.goal_instances (goal_instance_id, goal_id, entity_id, group_id, start_date, end_date, current_progress, is_active, is_complete, steps_contributed, coins_contributed, steps_needed, coins_needed, dollar_amount, completed_at, pacing_status, created_at, updated_at)
VALUES (999003, 999, 1, NULL, CURRENT_DATE - INTERVAL '1 day', CURRENT_DATE + INTERVAL '29 days', 550, true, false, 0, 550, 0, 500, 0.00, NULL, 'ON_TRACK', NOW(), NOW());

INSERT INTO public.user_goals (user_goal_id, user_id, goal_instance_id, allocation_pct, joined_at, left_at, is_active, is_complete, completed_at, target_type, target_amount, current_steps_progress, current_coins_progress, tier_level)
VALUES (999803, 1, 999003, 0.00, NOW() - INTERVAL '1 day', NULL, true, false, NULL, 'COINS', 500, 0, 550, 1);

// TODO delete everything
-- ======================================================================
-- STEP 5: FINAL POST-TEST TEARDOWN (Executes after processing finishes)
-- ======================================================================
-- Note: If running inside an integrated app test, add an explicit pause or validation step right here before these drop queries run.
DELETE FROM public.payout_batches WHERE goal_instance_id IN (999011, 999012, 999013, 999014, 999015, 999016, 999017, 999001, 999002, 999003);
DELETE FROM public.user_goals WHERE user_goal_id IN (999801, 999802, 999803);
DELETE FROM public.goal_instances WHERE goal_instance_id IN (999011, 999012, 999013, 999014, 999015, 999016, 999017, 999001, 999002, 999003);
DELETE FROM public.goals WHERE goal_id = 999;
DELETE FROM public.achievements WHERE achievement_id IN (999901, 999902);
/*

    /**
     * Cascades parent campaign completions down to individual participation records.
     * Finds active user goal entries linked to group or entity challenges that closed during
     * this batch run, marks them as complete, deactivates them, and stamps their resolution timestamp
     * to match the parent instance completion date.
     *
     * @return the absolute count of individual user goal rows safely synced and closed [3]
     */


/* TODO newest
-- ======================================================================
-- STEP 1: INITIAL CLEANUP (Teardown previous test cycles cleanly)
-- ======================================================================
-- Clean child join tables first to satisfy foreign key constraints
DELETE FROM public.user_achievements WHERE achievement_id IN (101, 999901, 999902) OR user_id = 1;
DELETE FROM public.entity_stats WHERE user_id = 1;
DELETE FROM public.group_stats WHERE user_id = 1;
DELETE FROM public.contributions WHERE user_id = 1;

-- Clean core testing tracking structures
DELETE FROM public.payout_batches WHERE goal_instance_id IN (999011, 999012, 999013, 999014, 999015, 999016, 999017, 999001, 999002, 999003);
DELETE FROM public.user_goals WHERE user_goal_id IN (999801, 999802, 999803);
DELETE FROM public.goal_instances WHERE goal_instance_id IN (999011, 999012, 999013, 999014, 999015, 999016, 999017, 999001, 999002, 999003);
DELETE FROM public.goals WHERE goal_id = 999;
DELETE FROM public.achievements WHERE achievement_id IN (101, 999901, 999902);


-- ======================================================================
-- STEP 2: BASE SYSTEM CATALOG DATA (Includes critical Java Target ID 101)
-- ======================================================================
INSERT INTO public.achievements (achievement_id, achievement_type, achievement, description, badge, required_steps, required_distance, required_coins)
VALUES
(101, 1, 'TEST_INDIVIDUAL_GOAL_BADGE', 'Real ID expected by Java pipeline assignment', 'badge_101.png', 0, 0, 0),
(999901, 1, 'TEST_USER_MILESTONE_BADGE', 'Obvious individual milestone badge', 'badge_test_user.png', 0, 0, 0),
(999902, 2, 'TEST_COMMUNAL_GOAL_BADGE', 'Obvious communal goal closure badge', 'badge_test_communal.png', 0, 0, 0)
ON CONFLICT (achievement_id) DO NOTHING;

-- Creates a test goal blueprint pointing straight to your active configuration structures
INSERT INTO public.goals (goal_id, goal_types_id, achievement_id, goal, description, completed_date, goal_lock_date, metric_type)
VALUES
(999, 1, 101, 'TEST_SYSTEM_TEMPLATE_GOAL', 'Obvious master baseline blueprint task', NULL, NULL, 'COINS')
ON CONFLICT (goal_id) DO NOTHING;


-- ======================================================================
-- STEP 3: MOCK RAW INGESTION PIPELINE DATA (Triggers steps 1, 2, and 3)
-- ======================================================================
-- Maps explicit fields + matching placeholder ad_session_id + jsonb allocation snapshot arrays
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


-- ======================================================================
-- STEP 4: SECTION A - DYNAMIC PACING TEST CONFIGURATIONS (Cases 1-7)
-- ======================================================================

-- 1. EXPECTS: 'NEAR_EXPIRATION_ALERT' (91% time, 10% coins)
INSERT INTO public.goal_instances (goal_instance_id, goal_id, entity_id, group_id, start_date, end_date, coins_contributed, coins_needed, steps_contributed, steps_needed, dollar_amount, is_active, is_complete, pacing_status, created_at, updated_at)
VALUES (999011, 999, 1, NULL, CURRENT_DATE - INTERVAL '91 days', CURRENT_DATE + INTERVAL '9 days', 100, 1000, 10000, 100000, 50.00, true, false, 'ON_TRACK', NOW(), NOW());

-- 2. EXPECTS: 'HIGH_RISK_FAILURE' (81% time, 70% coins)
INSERT INTO public.goal_instances (goal_instance_id, goal_id, entity_id, group_id, start_date, end_date, coins_contributed, coins_needed, steps_contributed, steps_needed, dollar_amount, is_active, is_complete, pacing_status, created_at, updated_at)
VALUES (999012, 999, 1, NULL, CURRENT_DATE - INTERVAL '81 days', CURRENT_DATE + INTERVAL '19 days', 700, 1000, 70000, 100000, 350.00, true, false, 'ON_TRACK', NOW(), NOW());

-- 3. EXPECTS: 'CRITICAL_LAGGING' (76% time, 60% coins)
INSERT INTO public.goal_instances (goal_instance_id, goal_id, entity_id, group_id, start_date, end_date, coins_contributed, coins_needed, steps_contributed, steps_needed, dollar_amount, is_active, is_complete, pacing_status, created_at, updated_at)
VALUES (999013, 999, 1, NULL, CURRENT_DATE - INTERVAL '76 days', CURRENT_DATE + INTERVAL '24 days', 600, 1000, 60000, 100000, 300.00, true, false, 'ON_TRACK', NOW(), NOW());

-- 4. EXPECTS: 'BEHIND_NEED_RECALCULATION' (52% time, 35% coins)
INSERT INTO public.goal_instances (goal_instance_id, goal_id, entity_id, group_id, start_date, end_date, coins_contributed, coins_needed, steps_contributed, steps_needed, dollar_amount, is_active, is_complete, pacing_status, created_at, updated_at)
VALUES (999014, 999, 1, NULL, CURRENT_DATE - INTERVAL '52 days', CURRENT_DATE + INTERVAL '48 days', 350, 1000, 35000, 100000, 175.00, true, false, 'ON_TRACK', NOW(), NOW());

-- 5. EXPECTS: 'SLIGHTLY_BEHIND' (26% time, 10% coins)
INSERT INTO public.goal_instances (goal_instance_id, goal_id, entity_id, group_id, start_date, end_date, coins_contributed, coins_needed, steps_contributed, steps_needed, dollar_amount, is_active, is_complete, pacing_status, created_at, updated_at)
VALUES (999015, 999, 1, NULL, CURRENT_DATE - INTERVAL '26 days', CURRENT_DATE + INTERVAL '74 days', 100, 1000, 10000, 100000, 50.00, true, false, 'ON_TRACK', NOW(), NOW());

-- 6. EXPECTS: 'ON_TRACK' (50% time, 55% coins)
INSERT INTO public.goal_instances (goal_instance_id, goal_id, entity_id, group_id, start_date, end_date, coins_contributed, coins_needed, steps_contributed, steps_needed, dollar_amount, is_active, is_complete, pacing_status, created_at, updated_at)
VALUES (999016, 999, 1, NULL, CURRENT_DATE - INTERVAL '50 days', CURRENT_DATE + INTERVAL '50 days', 550, 1000, 55000, 100000, 275.00, true, false, 'ON_TRACK', NOW(), NOW());

-- 7. EXPECTS: 'ON_TRACK' (24% time)
INSERT INTO public.goal_instances (goal_instance_id, goal_id, entity_id, group_id, start_date, end_date, coins_contributed, coins_needed, steps_contributed, steps_needed, dollar_amount, is_active, is_complete, pacing_status, created_at, updated_at)
VALUES (999017, 999, 1, NULL, CURRENT_DATE - INTERVAL '24 days', CURRENT_DATE + INTERVAL '76 days', 50, 1000, 5000, 100000, 25.00, true, false, 'ON_TRACK', NOW(), NOW());


-- ======================================================================
-- STEP 5: SECTION B - IMMEDIATE CLOSURES & ACCOLADE MINTING (Cases 8-10)
-- ======================================================================

-- 8. EXPECTS IMMEDIATE ENTITY GOAL CLOSURE, SYNCHRONIZED TEAM RESET, & PAYOUT DRAFT
INSERT INTO public.goal_instances (goal_instance_id, goal_id, entity_id, group_id, start_date, end_date, current_progress, is_active, is_complete, steps_contributed, coins_contributed, steps_needed, coins_needed, dollar_amount, completed_at, pacing_status, created_at, updated_at)
VALUES (999001, 999, 1, NULL, CURRENT_DATE - INTERVAL '10 days', CURRENT_DATE + INTERVAL '20 days', 1000, true, false, 100000, 1000, 100000, 1000, 500.00, NULL, 'ON_TRACK', NOW(), NOW());

INSERT INTO public.user_goals (user_goal_id, user_id, goal_instance_id, allocation_pct, joined_at, left_at, is_active, is_complete, completed_at, target_type, target_amount, current_steps_progress, current_coins_progress, tier_level)
VALUES (999801, 1, 999001, 100.00, NOW() - INTERVAL '5 days', NULL, true, false, NULL, 'COINS', 1000, 50000, 550, 1);


-- 9. EXPECTS IMMEDIATE GROUP GOAL CLOSURE & SOCIAL ACHIEVEMENT DISTRIBUTION
INSERT INTO public.goal_instances (goal_instance_id, goal_id, entity_id, group_id, start_date, end_date, current_progress, is_active, is_complete, steps_contributed, coins_contributed, steps_needed, coins_needed, dollar_amount, completed_at, pacing_status, created_at, updated_at)
VALUES (999002, 999, NULL, 1, CURRENT_DATE - INTERVAL '5 days', CURRENT_DATE + INTERVAL '25 days', 2000, true, false, 200000, 2000, 200000, 2000, 0.00, NULL, 'ON_TRACK', NOW(), NOW());

INSERT INTO public.user_goals (user_goal_id, user_id, goal_instance_id, allocation_pct, joined_at, left_at, is_active, is_complete, completed_at, target_type, target_amount, current_steps_progress, current_coins_progress, tier_level)
VALUES (999802, 1, 999002, 100.00, NOW() - INTERVAL '3 days', NULL, true, false, NULL, 'COINS', 2000, 20000, 300, 1);


-- 10. EXPECTS IMMEDIATE INDIVIDUAL SOLO GOAL CLOSURE & BADGE MINTING
INSERT INTO public.goal_instances (goal_instance_id, goal_id, entity_id, group_id, start_date, end_date, current_progress, is_active, is_complete, steps_contributed, coins_contributed, steps_needed, coins_needed, dollar_amount, completed_at, pacing_status, created_at, updated_at)
VALUES (999003, 999, 1, NULL, CURRENT_DATE - INTERVAL '1 day', CURRENT_DATE + INTERVAL '29 days', 550, true, false, 0, 550, 0, 500, 0.00, NULL, 'ON_TRACK', NOW(), NOW());

INSERT INTO public.user_goals (user_goal_id, user_id, goal_instance_id, allocation_pct, joined_at, left_at, is_active, is_complete, completed_at, target_type, target_amount, current_steps_progress, current_coins_progress, tier_level)
VALUES (999803, 1, 999003, 0.00, NOW() - INTERVAL '1 day', NULL, true, false, NULL, 'COINS', 500, 0, 550, 1);


 */
-- step 2
INSERT INTO public.achievements(
    achievement_type, achievement, description, badge, required_steps, required_distance, required_coins)
VALUES ( 1, '10k STEPS', 'User walked 10,000 steps', 'photo', 10000, null, null);
INSERT INTO public.achievements(
    achievement_type, achievement, description, badge, required_steps, required_distance, required_coins)
VALUES ( 2, '10k Dollars', 'Users contribute $10,000', 'photo', null, null,100000000 );



INSERT INTO public.affiliations(
    name)
VALUES ( 'Sun Belt');
INSERT INTO public.affiliations(
    name)
VALUES ( 'D1 Football');
-- INSERT INTO public.contribution_statuses(
--     contribution_status_id,code, label, description)
-- VALUES (1,'PENDING', 'Pending', 'Contribution is pending verification');
-- INSERT INTO public.contribution_statuses(
--     contribution_status_id,code, label, description)
-- VALUES (2,'VERIFIED', 'Verified', 'Contribution has been verified');
-- INSERT INTO public.contribution_statuses(
--     contribution_status_id,code, label, description)
-- VALUES (3,'PAID', 'Paid', 'Contribution has been paid to entity');
-- INSERT INTO public.contribution_statuses(
--     contribution_status_id,code, label, description)
-- VALUES (4,'REJECTED', 'Rejected', 'Contribution is rejected');
-- INSERT INTO public.contribution_statuses(
--     contribution_status_id,code, label, description)
-- VALUES (5,'ALLOCATED', 'ALLOCATED', 'contribution metrics have been read, calculated, and split out into entity_stats and group_stats, but the funds have not yet been approved or paid out');
-- ======================================================================
-- STEP 2: SEED DATA CATALOG DEFINITIONS
-- ======================================================================
-- 1. Contribution Status Layer (Includes new ALLOCATED state)
-- ======================================================================
-- STATUS CATALOG UPSERT MATRIX (PASTE DIRECTLY IN DB)
-- ======================================================================
    INSERT INTO public.contribution_statuses (contribution_status_id, code, label, description) VALUES
    (1, 'PENDING', 'Pending Verification', 'Incoming raw ad sessions awaiting anti-cheat clearance'),
    (2, 'VERIFIED', 'Verified Records', 'Cleared for pipeline distribution computations'),
    (5, 'ALLOCATED', 'Allocated to Stats', 'Metrics extracted and pushed to entity/group ledger layers'),
    (3, 'PAID', 'Settled Ledger Paid', 'Financial bank wire confirmation attached to parent batch run'),
    (4, 'REJECTED', 'Rejected Fraud', 'System dropped due to anti-cheat anomalies')
    ON CONFLICT (contribution_status_id) DO UPDATE SET
    code = EXCLUDED.code,
    label = EXCLUDED.label,
    description = EXCLUDED.description;


-- 2. Pacing Status Layer
INSERT INTO public.pacing_statuses (pacing_status_id, code, label, description) VALUES
(1, 'ON_TRACK', 'On Track', 'Campaign progress meets or exceeds expectations'),
(2, 'SLIGHTLY_BEHIND', 'Slightly Behind', 'Campaign metrics are falling just below target metrics'),
(3, 'BEHIND_NEED_RECALCULATION', 'Needs Adjustment', 'Significant metrics lag requires admin intervention'),
(4, 'CRITICAL_LAGGING', 'Critical Delays', 'High risk of campaign failure before end date'),
(5, 'HIGH_RISK_FAILURE', 'High Risk Failure', 'Severe metrics lag as timeline nears completion'),
(6, 'NEAR_EXPIRATION_ALERT', 'Near Expiration', 'Campaign is ending soon but targets are unmet'),
(7, 'COMPLETED', 'Campaign Targets Achieved', 'Core milestones completed, timeline modified to today'),
(8, 'CLOSED_PENDING_AUDIT', 'Audit Queue', 'Locked timeline undergoing anti-cheat verification checks'),
(9, 'SETTLED', 'Treasury Settlement Completed', 'Payout batches generated and finalized by treasury release')
ON CONFLICT (pacing_status_id) DO NOTHING;

-- 3. Individual User Goals Layer (Replacing confusion with clear tracking milestones)
INSERT INTO public.user_goal_statuses (user_goal_status_id, code, label, description) VALUES
(1, 'TRACKING', 'Active Progress Tracking', 'User is actively logging metrics toward solo milestone targets'),
(2, 'METRICS_MET', 'Milestone Fulfilled', 'Target metrics hit, awaiting contract evaluation routine'),
(3, 'ACCOLADE_MINTED', 'Badges Minted', 'Achievements assigned, pending historical backup archival process'),
(4, 'ARCHIVED_SUCCESS', 'Archived Completed', 'Cleanly processed individual contract closed out'),
(5, 'EXCLUDED_SYSTEM_RESET', 'Excluded from Execution', 'Instance was bypassed or reset by administrative request')
ON CONFLICT (user_goal_status_id) DO NOTHING;


-- ======================================================================
-- STEP 4: EVOLVE STRUCTURAL TABLES WITH ENHANCED METADATA
-- ======================================================================
-- Add status reference columns to operational tables
ALTER TABLE public.goal_instances
    ADD COLUMN pacing_status_id INT REFERENCES public.pacing_statuses(pacing_status_id) DEFAULT 1;

ALTER TABLE public.user_goals
    ADD COLUMN user_goal_status_id INT REFERENCES public.user_goal_statuses(user_goal_status_id) DEFAULT 1;

-- Add tracking columns to statistics tables to ensure data is read exactly once
ALTER TABLE public.entity_stats
    ADD COLUMN is_processed BOOLEAN DEFAULT false,
    ADD COLUMN payout_batch_id BIGINT;

ALTER TABLE public.group_stats
    ADD COLUMN is_processed BOOLEAN DEFAULT false;

INSERT INTO public.entity_types(
    type, description)
VALUES ('SCHOOL_DIRECT', 'Pay directly to school athletic program');

INSERT INTO public.entity_types(
    type, description)
VALUES ('Collective', 'School Collective');


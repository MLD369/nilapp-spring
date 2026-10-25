-- ============================================================================
-- 1. POPULATE AFFILIATION_TYPES (AND STORE IDS IN VARIABLES)
-- ============================================================================

DO $$
DECLARE
v_fed_id INT;
    v_league_id INT;
    v_gov_id INT;
    v_comp_id INT;
    v_div_id INT;
    v_subdiv_id INT;
    v_conf_id INT;
    v_sport_id INT;
    v_geo_id INT;
    v_nat_id INT;
    v_event_id INT;
    v_charity_cat_id INT;
    v_org_net_id INT;
BEGIN

    -- Insert Types and capture IDs
INSERT INTO public.affiliation_types (name, description)
VALUES ('Federation', 'Global governing body')
    RETURNING affiliation_type_id INTO v_fed_id;

INSERT INTO public.affiliation_types (name, description)
VALUES ('League', 'Named league structure')
    RETURNING affiliation_type_id INTO v_league_id;

INSERT INTO public.affiliation_types (name, description)
VALUES ('Governing Body', 'The highest level of international or domestic regulation and oversight.')
    RETURNING affiliation_type_id INTO v_gov_id;

INSERT INTO public.affiliation_types (name, description)
VALUES ('Competition Level', 'The internal organizational status of a specific athletic program or team.')
    RETURNING affiliation_type_id INTO v_comp_id;

INSERT INTO public.affiliation_types (name, description)
VALUES ('Division', 'Categorization based on school sizing, athletic scholarship funding, and competitive tiers.')
    RETURNING affiliation_type_id INTO v_div_id;

INSERT INTO public.affiliation_types (name, description)
VALUES ('Sub-Division', 'Tactical performance or tier splits within a specific division (primarily used for NCAA football infrastructure).')
    RETURNING affiliation_type_id INTO v_subdiv_id;

INSERT INTO public.affiliation_types (name, description)
VALUES ('Conference', 'Primary localized competitive leagues where teams play scheduled regular-season games.')
    RETURNING affiliation_type_id INTO v_conf_id;

INSERT INTO public.affiliation_types (name, description)
VALUES ('Sport', 'The specific athletic discipline or game played by a team or program.')
    RETURNING affiliation_type_id INTO v_sport_id;

INSERT INTO public.affiliation_types (name, description)
VALUES ('Geographic Region', 'Regional boundaries used for scheduling, travel conferences, tournaments, or qualifiers, or non-profit operational footprints.')
    RETURNING affiliation_type_id INTO v_geo_id;

INSERT INTO public.affiliation_types (name, description)
VALUES ('National Program', 'Country-representative governing bodies and their respective selection pools for international play.')
    RETURNING affiliation_type_id INTO v_nat_id;

INSERT INTO public.affiliation_types (name, description)
VALUES ('Event Series', 'Major multi-sport or single-sport global championship tournaments and events.')
    RETURNING affiliation_type_id INTO v_event_id;

INSERT INTO public.affiliation_types (name, description)
VALUES ('Charity Category', 'The core focus, cause, or industry vertical that the non-profit organization services.')
    RETURNING affiliation_type_id INTO v_charity_cat_id;

INSERT INTO public.affiliation_types (name, description)
VALUES ('Organization Network', 'The real-world parent organization or global non-profit brand entity.')
    RETURNING affiliation_type_id INTO v_org_net_id;


-- ============================================================================
-- 2. POPULATE AFFILIATIONS (WITH ORIGINAL AND ADDED EXAMPLES)
-- ============================================================================

-- Federation
INSERT INTO public.affiliations (name, affiliation_type_id, description) VALUES
('FIFA', v_fed_id, 'International Association Football Federation'),
('FIBA', v_fed_id, 'International Basketball Federation'),
('World Athletics', v_fed_id, 'International governing body for track and field athletics'),
('FIDE', v_fed_id, 'International Chess Federation'),
('World Aquatics', v_fed_id, 'International governing body for water sports');

-- League
INSERT INTO public.affiliations (name, affiliation_type_id, description) VALUES
('Ivy League', v_league_id, 'Elite academic and athletic collegiate league group'),
('Patriot League', v_league_id, 'Collegiate athletic league of high-academic institutions'),
('Pioneer Football League', v_league_id, 'NCAA Football Championship Subdivision conference'),
('Colonial Athletic Association', v_league_id, 'Multi-sport NCAA division league structure');

-- Governing Body
INSERT INTO public.affiliations (name, affiliation_type_id, description) VALUES
('NCAA', v_gov_id, 'National Collegiate Athletic Association'),
('NAIA', v_gov_id, 'National Association of Intercollegiate Athletics'),
('NJCAA', v_gov_id, 'National Junior College Athletic Association'),
('IOC', v_gov_id, 'International Olympic Committee'),
('USOPC', v_gov_id, 'United States Olympic & Paralympic Committee'),
('NFHS', v_gov_id, 'National Federation of State High School Associations'),
('CCCAA', v_gov_id, 'California Community College Athletic Association');

-- Competition Level
INSERT INTO public.affiliations (name, affiliation_type_id, description) VALUES
('Varsity', v_comp_id, 'Primary high-performance athletic representation for an institution'),
('Club', v_comp_id, 'Student-led competitive collegiate level athletics'),
('Intramural', v_comp_id, 'Internal recreational athletic competitions within a single campus'),
('Junior Varsity', v_comp_id, 'Secondary developmental athletic team tier'),
('Extramural', v_comp_id, 'Inter-campus recreational club tournament circuits');

-- Division
INSERT INTO public.affiliations (name, affiliation_type_id, description) VALUES
('NCAA Division I', v_div_id, 'Highest level of NCAA collegiate competition with maximum scholarship quotas'),
('NCAA Division II', v_div_id, 'Intermediate level of NCAA competition balancing academic and athletic funding'),
('NCAA Division III', v_div_id, 'NCAA competitive tier prioritizing academic experience with zero athletic scholarships'),
('NAIA Division I', v_div_id, 'Top level tier of NAIA school athletic competition sets'),
('NAIA Division II', v_div_id, 'Secondary tier of NAIA school athletic competition sets'),
('NJCAA Division I', v_div_id, 'Junior college athletic tier with full athletic scholarship offerings'),
('NJCAA Division II', v_div_id, 'Junior college athletic tier limited to partial tuition scholarship offerings'),
('NJCAA Division III', v_div_id, 'Junior college athletic tier offering zero athletic aid packages'),
('USCAA Division I', v_div_id, 'United States Collegiate Athletic Association primary tier'),
('NCCAA Division I', v_div_id, 'National Christian College Athletic Association primary tier');

-- Sub-Division
INSERT INTO public.affiliations (name, affiliation_type_id, description) VALUES
('FBS', v_subdiv_id, 'Football Bowl Subdivision (highest tier of NCAA DI football)'),
('FCS', v_subdiv_id, 'Football Championship Subdivision (bracketed playoff tier of NCAA DI football)'),
('Sprint Football', v_subdiv_id, 'Weight-restricted developmental football tier variant');

-- Conference
INSERT INTO public.affiliations (name, affiliation_type_id, description) VALUES
('SEC', v_conf_id, 'Southeastern Conference'),
('ACC', v_conf_id, 'Atlantic Coast Conference'),
('Big Ten', v_conf_id, 'Big Ten Conference'),
('Big 12', v_conf_id, 'Big 12 Conference'),
('Pac-12', v_conf_id, 'Pacific-12 Conference'),
('Sun Belt', v_conf_id, 'Sun Belt Conference'),
('Mountain West', v_conf_id, 'Mountain West Conference'),
('AAC', v_conf_id, 'American Athletic Conference'),
('MAC', v_conf_id, 'Mid-American Conference'),
('Conference USA', v_conf_id, 'Conference USA athletic organization league'),
('Big East', v_conf_id, 'Big East multi-sport basketball focused conference'),
('WAC', v_conf_id, 'Western Athletic Conference'),
('ASUN', v_conf_id, 'ASUN Conference league network'),
('Horizon League', v_conf_id, 'Horizon League midwest athletic system'),
('Missouri Valley', v_conf_id, 'Missouri Valley Conference'),
('West Coast Conference', v_conf_id, 'West Coast Conference athletic system'),
('SEC Academic Consortium', v_conf_id, 'Academic division extension of athletic conferences'),
('NESCAC', v_conf_id, 'New England Small College Athletic Conference - Elite Division III');

-- Sport
INSERT INTO public.affiliations (name, affiliation_type_id, description) VALUES
('Football', v_sport_id, 'Gridiron Football programs'),
('Basketball', v_sport_id, 'Court Basketball programs'),
('Baseball', v_sport_id, 'Men''s Baseball diamond programs'),
('Softball', v_sport_id, 'Women''s Softball diamond programs'),
('Track & Field', v_sport_id, 'Outdoor and Indoor Track and Field sports disciplines'),
('Cross Country', v_sport_id, 'Long-distance cross country running programs'),
('Soccer', v_sport_id, 'Association football sports networks'),
('Volleyball', v_sport_id, 'Indoor and beach volleyball programs'),
('Swimming', v_sport_id, 'Aquatic swimming and diving athletic groups'),
('Wrestling', v_sport_id, 'Mat wrestling sports divisions'),
('Tennis', v_sport_id, 'Racket tennis athletic sport lines'),
('Golf', v_sport_id, 'Course golf programs'),
('Lacrosse', v_sport_id, 'Field lacrosse programs'),
('Ice Hockey', v_sport_id, 'Rink ice hockey athletic groups'),
('Rowing', v_sport_id, 'Crew rowing sports divisions');
-- Geographic Region
INSERT INTO public.affiliations (name, affiliation_type_id, description) VALUES
('East Coast', v_geo_id, 'East Coast region territory parameters'),
('West Coast', v_geo_id, 'West Coast region territory parameters'),
('Midwest', v_geo_id, 'Midwestern state territorial borders'),
('Southeast', v_geo_id, 'Southeastern US operational bounds'),
('Southwest', v_geo_id, 'Southwestern geographic operational sectors'),
('Northeast', v_geo_id, 'Northeastern state territorial configurations'),
('Mid-Atlantic', v_geo_id, 'Mid-Atlantic coastal states region'),
('Mountain Region', v_geo_id, 'High mountain territory states sector'),
('Pan-American', v_geo_id, 'Americas wide multinational continental region bounds'),
('International', v_geo_id, 'Cross border multi-country operational footprint mapping'),
('Global', v_geo_id, 'Worldwide operational status mapping across all populated territories'),
('Pacific Northwest', v_geo_id, 'Northwest coastal zone region coverage'),
('EMEA', v_geo_id, 'Europe, Middle East, and Africa corporate/charity regional zone'),
('APAC', v_geo_id, 'Asia-Pacific operational zone mapping');
-- National Program
INSERT INTO public.affiliations (name, affiliation_type_id, description) VALUES
('USA Track & Field', v_nat_id, 'US national sport governing track network'),
('USA Basketball', v_nat_id, 'US national basketball governing body group'),
('USA Swimming', v_nat_id, 'US national swimming athletic pool board'),
('USA Wrestling', v_nat_id, 'US national amateur wrestling board system'),
('USA Soccer', v_nat_id, 'United States Soccer Federation National Teams umbrella'),
('Team Canada', v_nat_id, 'Canadian National Olympic athlete delegation'),
('Olympic Selection Pool', v_nat_id, 'Active candidate tier for direct Olympic roster consideration pathways'),
('USA Gymnastics', v_nat_id, 'US national gymnastics athletic pool board'),
('Team GB', v_nat_id, 'Great Britain Olympic athlete delegation');
--Event Series
INSERT INTO public.affiliations (name, affiliation_type_id, description) VALUES
('Olympic Games', v_event_id, 'Summer and Winter quadrennial global sporting spectacles'),
('World Cup', v_event_id, 'Global championship single-sport tournaments'),
('Pan-Am Games', v_event_id, 'Americas regional Olympic style athletic competition events'),
('Paralympic Games', v_event_id, 'Parallel multi-sport games for athletes with physical disabilities'),
('Commonwealth Games', v_event_id, 'Quadrennial multi-sport event for Commonwealth nations');
-- Charity Category
INSERT INTO public.affiliations (name, affiliation_type_id, description) VALUES
('Health & Wellness', v_charity_cat_id, 'Medical services, disease research, mental health aid programs'),
('Education & Literacy', v_charity_cat_id, 'Schools, tuition programs, adult literacy training setups'),
('Environmental / Conservation', v_charity_cat_id, 'Wildlife protections, parks preservation, climate action services'),
('Community Development', v_charity_cat_id, 'Urban renewal, job training pipelines, local public space investments'),
('Disaster Relief', v_charity_cat_id, 'Emergency responses, food supply chains post-crises, housing rebuild operations'),
('Animal Welfare', v_charity_cat_id, 'Animal shelters, anti-cruelty enforcement, rescue networks'),
('Arts & Culture', v_charity_cat_id, 'Museums, historic preservation, public media broadcasting'),
('Civil Rights & Advocacy', v_charity_cat_id, 'Legal defense funds, voter registration, equality tracking systems');
-- Organization Network
INSERT INTO public.affiliations (name, affiliation_type_id, description) VALUES
('American Red Cross', v_org_net_id, 'Emergency assistance, disaster relief, and disaster preparedness organization'),
('UNICEF', v_org_net_id, 'United Nations Children''s Fund programmatic umbrella'),
('Habitat for Humanity', v_org_net_id, 'Global housing development non-profit system'),
('Feeding America', v_org_net_id, 'United States food bank charity network system'),
('Salvation Army', v_org_net_id, 'International christian charitable organization framework'),
('Doctors Without Borders', v_org_net_id, 'International medical humanitarian non-governmental organization'),
('St. Jude Children''s Research Hospital', v_org_net_id, 'Pediatric treatment and research facility network'),
('Goodwill Industries', v_org_net_id, 'Community-based workforce development organization network'),
('The Nature Conservancy', v_org_net_id, 'Global environmental non-profit network body');
END $$;

-- TODO make sure these are working
-- 1. Create the entity_stats table
CREATE TABLE entity_stats (
                              entity_stat_id BIGSERIAL PRIMARY KEY,
                              contribution_id BIGINT NOT NULL,
                              entity_id BIGINT NOT NULL,
                              user_id BIGINT NOT NULL,
                              coins BIGINT DEFAULT 0,
                              steps BIGINT DEFAULT 0,
                              allocation_percentage INT NOT NULL,
                              created_at TIMESTAMP DEFAULT NOW(),

    -- Foreign Key Constraints
                              CONSTRAINT fk_entity_stats_contribution FOREIGN KEY (contribution_id) REFERENCES contributions(contribution_id) ON DELETE CASCADE,
                              CONSTRAINT fk_entity_stats_entity FOREIGN KEY (entity_id) REFERENCES entities(entity_id),
                              CONSTRAINT fk_entity_stats_user FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- 2. Create the group_stats table
CREATE TABLE group_stats (
                             group_stat_id BIGSERIAL PRIMARY KEY,
                             contribution_id BIGINT NOT NULL,
                             group_id BIGINT NOT NULL,
                             user_id BIGINT NOT NULL,
                             coins BIGINT DEFAULT 0,
                             steps BIGINT DEFAULT 0,
                             allocation_percentage INT NOT NULL,
                             created_at TIMESTAMP DEFAULT NOW(),

    -- Foreign Key Constraints
                             CONSTRAINT fk_group_stats_contribution FOREIGN KEY (contribution_id) REFERENCES contributions(contribution_id) ON DELETE CASCADE,
                             CONSTRAINT fk_group_stats_group FOREIGN KEY (group_id) REFERENCES groups(group_id),
                             CONSTRAINT fk_group_stats_user FOREIGN KEY (user_id) REFERENCES users(user_id)
);
-- Speeds up calculating goal progress for specific entities over time windows
CREATE INDEX idx_entity_stats_lookup ON entity_stats (entity_id, created_at);

-- Speeds up calculating goal progress for specific friend groups over time windows
CREATE INDEX idx_group_stats_lookup ON group_stats (group_id, created_at);

-- Prevents slow cascading checks when looking up a specific user's historic allocations
CREATE INDEX idx_entity_stats_user ON entity_stats (user_id);
CREATE INDEX idx_group_stats_user ON group_stats (user_id);

-- ALTER TABLE user_achievements
--     ADD CONSTRAINT unique_user_achievement_run UNIQUE (user_id, achievement_id, earned_at);
-- ALTER TABLE public.user_achievements DROP CONSTRAINT IF EXISTS unique_user_achievement_run;
ALTER TABLE public.user_achievements
    ADD CONSTRAINT unique_user_goal_instance_badge UNIQUE (user_id, goal_instance_id);


ALTER TABLE goal_instances ADD COLUMN completed_at TIMESTAMP;

-- Adds customizable validation rules per challenge
ALTER TABLE entity_achievements ADD COLUMN min_coins_required INT DEFAULT NULL;
ALTER TABLE group_achievements ADD COLUMN min_coins_required INT DEFAULT NULL;
ALTER TABLE user_streaks
    ADD COLUMN total_shields_earned INT DEFAULT 0,
ADD COLUMN total_shields_used INT DEFAULT 0,
ADD COLUMN total_video_savers_used INT DEFAULT 0;

ALTER TABLE user_goals
    ADD COLUMN target_type VARCHAR(20) DEFAULT 'COINS', -- 'STEPS' or 'COINS'
ADD COLUMN target_amount INT NOT NULL DEFAULT 1000, -- e.g., 1,000 coins needed
ADD COLUMN current_steps_progress INT DEFAULT 0,
ADD COLUMN current_coins_progress INT DEFAULT 0,
ADD COLUMN tier_level INT DEFAULT 1; -- For progressive tier goals (Level 1, Level 2, etc.) TODO not sure if needed but could be used as indicator for need recalculation

ALTER TABLE goals
    ADD COLUMN metric_type VARCHAR(20) DEFAULT 'COINS'; -- 'STEPS' or 'COINS'

-- TODO this is a breaking change make sure it works
-- this change makes it so a user can only join a goal once that is active
-- that mean the batch need to be self reliant on making new levels so we do not need to manually create each goal everytime
-- check there are no duplicat user goals
CREATE UNIQUE INDEX idx_unique_active_user_goal
    ON user_goals (user_id, goal_instance_id)
    WHERE is_active = true;

BEGIN;

-- 1. Add precise revenue tracking fields STRICTLY to your entity layers
ALTER TABLE public.entity_stats ADD COLUMN IF NOT EXISTS revenue_generated DECIMAL(15,6) DEFAULT 0.000000;
ALTER TABLE public.entity_stats ADD COLUMN IF NOT EXISTS company_profit_retained DECIMAL(15,6) DEFAULT 0.000000;

-- 2. Add an analytical health indicator to track ad delivery pacing metrics on campaigns
ALTER TABLE public.goal_instances ADD COLUMN IF NOT EXISTS pacing_status VARCHAR(30) DEFAULT 'ON_TRACK';
ALTER TABLE public.goal_instances ADD COLUMN IF NOT EXISTS completed_at TIMESTAMP;

-- 3. Link your payout tracking batches directly back to your completed campaign identifiers
ALTER TABLE public.payout_batches ADD COLUMN IF NOT EXISTS goal_instance_id BIGINT;

-- 4. Clean up daily stats definitions to distinguish total walking volume from claimed volume
ALTER TABLE public.daily_stats RENAME COLUMN total_steps TO total_claimed_steps;
ALTER TABLE public.daily_stats ADD COLUMN IF NOT EXISTS total_device_steps INT DEFAULT 0;

-- 5. Establish unique partial index to allow users to redo individual goals manually
DROP INDEX IF EXISTS idx_unique_active_user_goal;
CREATE UNIQUE INDEX idx_unique_active_user_goal
    ON public.user_goals (user_id, goal_instance_id)
    WHERE is_active = true;

-- 6. Add composite tracking constraints for repeatable milestone runs
ALTER TABLE public.user_achievements DROP CONSTRAINT IF EXISTS unique_user_achievement_run;
ALTER TABLE public.user_achievements ADD CONSTRAINT unique_user_achievement_run UNIQUE (user_id, achievement_id, earned_at);

-- 7. High-Performance Optimization Indexes for high-volume execution windows
CREATE INDEX IF NOT EXISTS idx_entity_stats_financials ON public.entity_stats (entity_id, created_at) INCLUDE (revenue_generated);
CREATE INDEX IF NOT EXISTS idx_contributions_pacing ON public.contributions (user_id, contribution_status_id, created_at);

COMMIT;


BEGIN;

-- 1. Add banking rails and settlement tracking metadata
ALTER TABLE public.payout_batches ADD COLUMN IF NOT EXISTS payment_method VARCHAR(20) DEFAULT 'ACH'; -- 'ACH', 'WIRE', 'CHECK'
ALTER TABLE public.payout_batches ADD COLUMN IF NOT EXISTS tracking_number VARCHAR(100);             -- Check check#, ACH trace id, wire reference
ALTER TABLE public.payout_batches ADD COLUMN IF NOT EXISTS payout_status VARCHAR(30) DEFAULT 'DRAFT'; -- 'DRAFT', 'PROCESSING', 'SETTLED', 'FAILED', 'PAID'
ALTER TABLE public.payout_batches ADD COLUMN IF NOT EXISTS total_coins_processed BIGINT DEFAULT 0;
ALTER TABLE public.payout_batches ADD COLUMN IF NOT EXISTS total_steps_processed BIGINT DEFAULT 0;
ALTER TABLE public.payout_batches ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT NOW();

COMMIT;

ALTER TABLE public.payout_batches
    ADD COLUMN IF NOT EXISTS recommended_payout_date DATE;
ALTER TABLE public.payout_batches ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT NOW();

BEGIN;

-- Convert the column type to match Hibernate's expectation
ALTER TABLE public.payout_batches
ALTER COLUMN entity_id TYPE BIGINT;

COMMIT;

ALTER TABLE public.payout_batches RENAME COLUMN total_steps_processed TO total_claimed_steps_processed;


ALTER TABLE public.goal_instances ALTER COLUMN pacing_status TYPE VARCHAR(50);

BEGIN;

-- 1. Inject audit timestamps with default parameters
ALTER TABLE public.goal_instances ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT NOW();
ALTER TABLE public.goal_instances ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT NOW();

-- 2. Build a performant sorting index for time-sensitive audits
CREATE INDEX IF NOT EXISTS idx_goal_instances_timestamps ON public.goal_instances (updated_at DESC);

COMMIT;
-- Add the missing column to your user_achievements table
ALTER TABLE public.user_achievements
    ADD COLUMN goal_instance_id BIGINT REFERENCES public.goal_instances(goal_instance_id);

-- // TODO payout batch needs something to stop duplicates payout batch id, enitity id , transaction ref, tracking number,
-- todo I DONT THINK THERE SHOULD BE DUPLICATES entity and goal instances  also the batch need to change the status of the goal isntance
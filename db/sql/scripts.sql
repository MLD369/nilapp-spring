-- Get user goals  goal_instances and goals for userid
SELECT
    ug.user_id,
    ug.goal_instance_id,
    ug.is_active AS user_goal_is_active,
    ug.is_complete AS user_goal_is_complete,
    ug.completed_at,
    gi.goal_id,
    gi.is_active AS instance_is_active,
    gi.is_complete AS instance_is_complete,
    g.goal,
    g.description
FROM user_goals AS ug
         JOIN goal_instances AS gi
              ON gi.goal_instance_id = ug.goal_instance_id
         JOIN   AS g
              ON gi.goal_id = g.goal_id
where ug.user_id = 1;


--  script to make default goals for goal instances
-- 1. Backfill 1 permanent goal_instance for all existing ENTITIES
INSERT INTO public.goal_instances (
    goal_id,
    entity_id,
    group_id,
    start_date,
    end_date,
    is_active,
    is_complete,
    steps_contributed,
    coins_contributed,
    steps_needed,
    coins_needed,
    dollar_amount,
    pacing_status_id
)
SELECT
    1, -- Your master default goal_id
    e.entity_id,
    NULL,
    CURRENT_DATE,
    NULL,
    true,
    false,
    0,
    0,
    NULL,
    NULL,
    0.00,
    1 -- Default pacing_status_id (adjust if your default status ID is 0)
FROM public.entities e
WHERE NOT EXISTS (
    SELECT 1 FROM public.goal_instances gi
    WHERE gi.entity_id = e.entity_id AND gi.goal_id = 1
);

-- 2. Backfill 1 permanent goal_instance for all existing GROUPS
INSERT INTO public.goal_instances (
    goal_id,
    entity_id,
    group_id,
    start_date,
    end_date,
    is_active,
    is_complete,
    steps_contributed,
    coins_contributed,
    steps_needed,
    coins_needed,
    dollar_amount,
    pacing_status_id
)
SELECT
    1, -- Your master default goal_id
    NULL,
    gr.group_id,
    CURRENT_DATE,
    NULL,
    true,
    false,
    0,
    0,
    NULL,
    NULL,
    0.00,
    1 -- Default pacing_status_id
FROM public.groups gr
WHERE NOT EXISTS (
    SELECT 1 FROM public.goal_instances gi
    WHERE gi.group_id = gr.group_id AND gi.goal_id = 1
);

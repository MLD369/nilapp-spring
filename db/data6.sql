
-- // TODO update the view to get the  entity or group name
CREATE OR REPLACE VIEW user_entity_stats_view AS
SELECT
    c.user_id,
    (ea->>'entityId')::BIGINT AS entity_id,
    to_char(c.created_at, 'YYYY-MM') AS month_label,
    to_char(c.created_at, 'YYYY') AS year_label,
    SUM(c.steps_contributed * ((ea->>'percentage')::NUMERIC / 100.0)) AS total_steps,
    SUM(c.coins_contributed * ((ea->>'percentage')::NUMERIC / 100.0)) AS total_coins,
    c.contribution_status_id,
    c.created_at
FROM contributions c
         CROSS JOIN LATERAL jsonb_array_elements(c.allocation_snapshot->'entityAllocations') ea
GROUP BY
    c.user_id,
    (ea->>'entityId'),
    month_label,
    year_label,
    c.contribution_status_id,
    c.created_at;
-- // TODO update the view to get the  entity or group name
CREATE OR REPLACE VIEW user_group_stats_view AS
SELECT
    c.user_id,
    (ga->>'groupId')::BIGINT AS group_id,
    to_char(c.created_at, 'YYYY-MM') AS month_label,
    to_char(c.created_at, 'YYYY') AS year_label,
    SUM(c.steps_contributed * ((ga->>'percentage')::NUMERIC / 100.0)) AS total_steps,
    SUM(c.coins_contributed * ((ga->>'percentage')::NUMERIC / 100.0)) AS total_coins,
    c.status_id,
    c.created_at
FROM contributions c
         CROSS JOIN LATERAL jsonb_array_elements(c.allocation_snapshot->'groupAllocations') ga
GROUP BY
    c.user_id,
    (ga->>'groupId'),
    month_label,
    year_label,
    c.status_id,
    c.created_at;

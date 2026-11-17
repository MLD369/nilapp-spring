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

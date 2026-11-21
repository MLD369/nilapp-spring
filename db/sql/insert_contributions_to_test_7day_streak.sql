INSERT INTO public.contributions (
    user_id,
    entity_id,
    campaign_id,
    steps_contributed,
    coins_contributed,
    conversion_rate,
    ad_session_id,
    created_at,
    payout_batch_id,
    contribution_status_id,
    allocation_snapshot,
    contribution_type_id,
    is_synced_to_daily_stats,
    is_synced_to_user_goals
)
SELECT
    1,                                                                 -- user_id
    NULL,                                                              -- entity_id
    NULL,                                                              -- campaign_id
    500,                                                               -- steps_contributed
    500,                                                               -- coins_contributed
    0.001,                                                             -- conversion_rate
    gen_random_uuid(),                                                 -- ad_session_id (random UUID)
    (CURRENT_DATE - (day_offset || ' days')::interval)
        + TIME '12:01:45.558337'
        + (claim_num || ' minutes')::interval,                         -- created_at (Nov 12 through Nov 18)
    NULL,                                                              -- payout_batch_id
    1,                                                                 -- contribution_status_id
    '{
      "groupAllocations": [
        {
          "goals": [
            {"share": 0, "title": "Walk 10k steps", "goalId": 1, "goalInstanceId": 8},
            {"share": 1, "title": "Walk 100k steps", "goalId": 2, "goalInstanceId": 9}
          ],
          "groupId": 2,
          "percentage": 51
        },
        {
          "goals": [
            {"share": 0.51, "title": "TEST_SYSTEM_TEMPLATE_GOAL", "goalId": 999, "goalInstanceId": 999002},
            {"share": 0.49, "title": "Walk 10k steps", "goalId": 1, "goalInstanceId": 4}
          ],
          "groupId": 1,
          "percentage": 49
        }
      ],
      "entityAllocations": [
        {
          "goals": [
            {"share": 0.08, "title": "TEST_SYSTEM_TEMPLATE_GOAL", "goalId": 999, "goalInstanceId": 999003},
            {"share": 0.08, "title": "TEST_SYSTEM_TEMPLATE_GOAL", "goalId": 999, "goalInstanceId": 999001},
            {"share": 0, "title": "Walk 100k steps", "goalId": 2, "goalInstanceId": 6},
            {"share": 0.84, "title": "Walk 10k steps", "goalId": 1, "goalInstanceId": 3}
          ],
          "entityId": 1,
          "percentage": 100
        },
        {
          "goals": [
            {"share": 0, "title": "Walk 100k steps", "goalId": 2, "goalInstanceId": 12},
            {"share": 0, "title": "Walk 10k steps", "goalId": 1, "goalInstanceId": 11}
          ],
          "entityId": 4,
          "percentage": 0
        }
      ]
    }'::jsonb,                                                         -- allocation_snapshot (remove ::jsonb if column is text/varchar)
    1,                                                                 -- contribution_type_id
    false,                                                             -- is_synced_to_daily_stats
    false                                                              -- is_synced_to_user_goals
FROM generate_series(2, 8) AS day_offset  -- Days 2..8 ago = Nov 18 down to Nov 12 (7 days before missed Nov 19)
         CROSS JOIN generate_series(1, 10) AS claim_num; -- 10 claims per day = 70 rows total



--  to test the script
SELECT (created_at AT TIME ZONE 'America/New_York')::date AS day, COUNT(*)
FROM public.contributions
WHERE user_id = 1
GROUP BY day
ORDER BY day DESC;


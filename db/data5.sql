-- step 5
INSERT INTO public.user_progress(
    user_goal_id, steps_contributed, coins_contributed, recorded_at)
VALUES ( 1, 10, 10, now());

INSERT INTO public.group_entities(
    group_id, entity_id)
VALUES ( 1, 1);
-- // TODO make sure this works
UPDATE contributions
SET allocation_snapshot = '{
  "entityAllocations": [
    { "entityId": 1, "percentage": 100 }
  ],
  "groupAllocations": [
    { "groupId": 1, "percentage": 100 }
  ]
}'::jsonb
WHERE contribution_id = 1;
UPDATE contributions
SET allocation_snapshot = '{
  "entityAllocations": [
    { "entityId": 1, "percentage": 50 },
    { "entityId": 2, "percentage": 50 }
  ],
  "groupAllocations": [
    { "groupId": 1, "percentage": 100 }
  ]
}'::jsonb
WHERE contribution_id = 2;
UPDATE contributions
SET allocation_snapshot = '{
  "entityAllocations": [
    { "entityId": 1, "percentage": 50 },
    { "entityId": 2, "percentage": 50 }
  ],
  "groupAllocations": [
    { "groupId": 1, "percentage": 100 }
  ]
}'::jsonb
WHERE contribution_id = 3;



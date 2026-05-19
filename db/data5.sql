-- step 5
INSERT INTO public.user_progress(
    user_goal_id, steps_contributed, coins_contributed, recorded_at)
VALUES ( 1, 10, 10, now());

INSERT INTO public.group_entities(
    group_id, entity_id)
VALUES ( 1, 1);
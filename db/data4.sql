-- step 4
INSERT INTO public.goal_instances(
    goal_id, entity_id, group_id, start_date, end_date, current_progress, is_active, is_complete)
VALUES (1, null, null, now(), '9999-12-12', 0, true, false);



INSERT INTO public.user_achievements(
    user_id, achievement_id, earned_at)
VALUES (1, 1, '2026-04-12');

INSERT INTO public.sponsor_campaigns(
    sponsor_id, entity_id, payout_per_step, payout_per_coin, start_date, end_date)
VALUES ( 1, 1, null, .0001, '2026-01-01',  '9999-12-31');


INSERT INTO public.daily_stats(
    user_id, date, total_steps, coins, distance_meters, device_source, sync_timestamp, calories, created_at)
VALUES (1, '2026-04-20', 500, 5, 0, 'phone','2026-04-20 14:30:00' , 0, '2026-04-20 14:30:00');
INSERT INTO public.daily_stats(
    user_id, date, total_steps, coins, distance_meters, device_source, sync_timestamp, calories, created_at)
VALUES (1, '2026-04-19', 500, 50, 0, 'phone','2026-04-19 14:30:00' , 0, '2026-04-19 14:30:00');

INSERT INTO public.contributions(
    user_id, entity_id, campaign_id, steps_contributed, coins_contributed, conversion_rate, ad_session_id,  payout_batch_id, status)
VALUES (1, 1, 1, 500, 5, .001, 'ad1', null, 1);

INSERT INTO public.friend_statuses(
    status, description)
VALUES ('REQUESTED', 'Friend request is pending acceptance');
INSERT INTO public.friend_statuses(
    status, description)
VALUES ('FRIENDS', 'Friend request accepted');
INSERT INTO public.friend_statuses(
    status, description)
VALUES ('BLOCKED_BY_USER', 'User with userId blocked user');
INSERT INTO public.friend_statuses(
    status, description)
VALUES ('UNFRIENDED', 'User has unfriended the user');
INSERT INTO public.friend_statuses(
    status, description)
VALUES ('BLOCKED_BY_FRIEND', 'User with Friend Id blocked user');

INSERT INTO public.friends(
    user_id, friend_id, status)
VALUES (1, 2, 1);
INSERT INTO public.friends(
    user_id, friend_id, status)
VALUES (1, 3, 1);

INSERT INTO public.groups(
    name)
VALUES ( 'Red suns');

INSERT INTO public.user_groups(
    group_id, user_id)
VALUES (1, 1);
INSERT INTO public.group_achievements(
    group_id, achievement_id, earned_at)
VALUES (1, 2, '2026-01-01');

INSERT INTO public.user_entities(
    entity_id, user_id)
VALUES (1, 1);
INSERT INTO public.user_entities(
    entity_id, user_id)
VALUES (1, 2);
INSERT INTO public.user_entities(
    entity_id, user_id)
VALUES (2, 1);
INSERT INTO public.user_goals(
    user_id, goal_instance_id, allocation_pct, joined_at, left_at, is_active, is_complete)
VALUES ( 1, 1, .5, now(), null, true, false);



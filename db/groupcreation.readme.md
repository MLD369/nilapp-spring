
1. Make group
   INSERT INTO public.groups(
   name)
   VALUES ( ?);
2. Make group_entities
   INSERT INTO public.group_entities(
   group_id, entity_id)
   VALUES ( ?, ?);
3. make goal instances
   INSERT INTO public.goal_instances(
   goal_id, entity_id, group_id, start_date, end_date, steps_needed, coins_needed, dollar_amount)
   VALUES ( ?, null, ?, now(), '9999-12-12', ?, ?, ?);
4. make group entities
   INSERT INTO public.group_entities(
   group_id, entity_id)
   VALUES ( ?, ?);

(optional) make for testing
1. MAKE group_achievements 
   INSERT INTO public.group_achievements(
   group_id, achievement_id, earned_at)
   VALUES ( ?, ?, now());
2. make user_groups
    INSERT INTO public.user_groups(
    group_id, user_id, joined_at, left_at)
    VALUES ( ?, ?, ?, ?);
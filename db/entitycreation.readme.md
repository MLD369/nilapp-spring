
1. Make entity
   INSERT INTO public.entities(
   name, abbreviation, associated_school, type, is_active, primary_color, secondary_color, sporting_affiliations, tax_id)
   VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?);
2. Make  entity_affiliations
   INSERT INTO public.entity_affiliations(
   entity_id, affiliation_id, created_at)
   VALUES ( ?, ?, ?);
3. make goal instances
   INSERT INTO public.goal_instances(
   goal_id, entity_id, group_id, start_date, end_date, steps_needed, coins_needed, dollar_amount)
   VALUES ( ?, ?, null, now(), '9999-12-12', ?, ?, ?);
4. make group entities
   INSERT INTO public.group_entities(
   group_id, entity_id)
   VALUES ( ?, ?);

(optional) make for testing
1. make entity_achievements
   INSERT INTO public.entity_achievements(
   entity_id, achievement_id, earned_at)
   VALUES ( ?, ?, ?);
2. user_entities
   INSERT INTO public.user_entities(
   entity_id, user_id, joined_at, left_at)
   VALUES ( ?, ?, ?, ?);



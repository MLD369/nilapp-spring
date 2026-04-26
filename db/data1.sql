-- step 1

INSERT INTO public.achievement_types(
    type, meaning)
VALUES ('MILESTONE', 'Based on user stats (steps, coins, distance) App Preset');
INSERT INTO public.achievement_types(
	type, meaning)
	VALUES ('Goal', 'Tied to a Entity goal');
INSERT INTO public.achievement_types(
	type, meaning)
	VALUES ('SEASONAL', 'Limited‑time events');
INSERT INTO public.achievement_types(
	type, meaning)
	VALUES ('SPECIAL', 'Manual or rare achievements EX. 100 MILES ON GAMEDAYS');
INSERT INTO public.goal_types(
    type, description)
VALUES ('App', 'App goal');
INSERT INTO public.goal_types(
    type, description)
VALUES ('ENTITY', 'Entity goal');
INSERT INTO public.goal_types(
    type, description)
VALUES ('GROUP', 'Group goal');



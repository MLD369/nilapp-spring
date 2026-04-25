-- step 3
INSERT INTO public.entities(
    name, abbreviation, associated_school, type, is_active, primary_color, secondary_color, tax_id)
VALUES ( 'Georgia State University Bleed Blue', 'GSU', 'Georgia State University', 2, true, 'blue', 'white', 'taxid');

INSERT INTO public.entities(
    name, abbreviation, associated_school, type, is_active, primary_color, secondary_color, tax_id)
VALUES ( 'Georgia State University Athletic Program', 'GSU', 'Georgia State University', 1, true, 'blue', 'white', 'taxid');


-- User 1: Riley Smith
INSERT INTO public.users(
    username, first_name, last_name, middle_name, email, phone, password, total_lifetime_coins, birthday, birth_year, created_at, updated_at, state, country, city, county, language, timezone, force_password_change, session_id)
VALUES ( 'riley10', 'Riley', 'Smith', NULL, 'riley10@example.com', '+1-555-206-9518', '$2b$12$KZe...',  1400, '1994-04-24', 1994, '2026-02-17 23:38:29', '2026-02-17 23:38:29', 'GA', 'USA', 'Atlanta', 'Fulton', 'en-US', 'America/New_York', FALSE, 'sess_9668');

-- User 2: Jordan Jones
INSERT INTO public.users(
    username, first_name, last_name, middle_name, email, phone, password, total_lifetime_coins, birthday, birth_year, created_at, updated_at, state, country, city, county, language, timezone, force_password_change, session_id)
VALUES ( 'jordan82', 'Jordan', 'Jones', NULL, 'jordan82@example.com', '+1-555-780-6103', '$2b$12$LXe...',  4840, '1986-06-01', 1986, '2025-08-05 23:38:29', '2025-08-05 23:38:29', 'WA', 'USA', 'Seattle', 'King', 'fr-FR', 'America/Los_Angeles', FALSE, 'sess_1504');

-- User 3: Alex Johnson
INSERT INTO public.users(
    username, first_name, last_name, middle_name, email, phone, password,  total_lifetime_coins, birthday, birth_year, created_at, updated_at, state, country, city, county, language, timezone, force_password_change, session_id)
VALUES ( 'alex44', 'Alex', 'Johnson', 'Blake', 'alex44@example.com', '+1-555-211-6712', '$2b$12$MXe...',  4972, '2001-04-20', 2001, '2025-07-21 23:38:29', '2025-07-21 23:38:29', 'IL', 'USA', 'Chicago', 'Cook', 'es-ES', 'America/Chicago', TRUE, 'sess_4560');




INSERT INTO public.sponsors(
    name, type, contact_email, is_active)
VALUES ( 'NilApp', 'Default', 'n/a ', 'true');
INSERT INTO public.sponsors(
    name, type, contact_email, is_active)
VALUES ( 'Coke', 'Company', 'coke@coke.com', 'false');
INSERT INTO public.sponsors(
    name, type, contact_email, is_active)
VALUES ( 'Pepsi', 'Company', 'pepsi@pepsi.com', 'true');

INSERT INTO public.entity_affiliations(
    entity_id, affiliation_id)
VALUES (1, 1);


INSERT INTO public.goals(
    entity_id, achievement_id, goal, description, completed_date, goal_lock_date)
VALUES ( 1, 2, 'Raise 10k USD', 'Raise $10,000 for Track Team Travel', '9999-12-31', '9999-12-31');



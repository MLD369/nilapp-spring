-- step 2
INSERT INTO public.achievements(
    achievement_type, achievement, description, badge, required_steps, required_distance, required_coins)
VALUES ( 1, '10k STEPS', 'User walked 10,000 steps', 'photo', 10000, null, null);
INSERT INTO public.achievements(
    achievement_type, achievement, description, badge, required_steps, required_distance, required_coins)
VALUES ( 2, '10k Dollars', 'Users contribute $10,000', 'photo', null, null,100000000 );



INSERT INTO public.affiliations(
    name)
VALUES ( 'Sun Belt');
INSERT INTO public.affiliations(
    name)
VALUES ( 'D1 Football');
INSERT INTO public.contribution_statuses(
    code, label, description)
VALUES ('PENDING', 'Pending', 'Contribution is pending verification');
INSERT INTO public.contribution_statuses(
    code, label, description)
VALUES ('VERIFIED', 'Verified', 'Contribution has been verified');
INSERT INTO public.contribution_statuses(
    code, label, description)
VALUES ('PAID', 'Paid', 'Contribution has been paid to entity');
INSERT INTO public.contribution_statuses(
    code, label, description)
VALUES ('REJECTED', 'Rejected', 'Contribution is rejected');

INSERT INTO public.entity_types(
    type, description)
VALUES ('SCHOOL_DIRECT', 'Pay directly to school athletic program');

INSERT INTO public.entity_types(
    type, description)
VALUES ('Collective', 'School Collective');

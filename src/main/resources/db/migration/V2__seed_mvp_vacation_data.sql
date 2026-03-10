INSERT INTO users (id, clerk_user_id, full_name, email, role, leader_id, active, created_at, updated_at)
VALUES
    (1, NULL, 'Laura HR', 'laura.hr@vacaciones.local', 'HR', NULL, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, NULL, 'Martin Leader', 'martin.leader@vacaciones.local', 'LEADER', 1, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (3, NULL, 'Sofia Employee', 'sofia.employee@vacaciones.local', 'EMPLOYEE', 2, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (4, NULL, 'Nicolas Employee', 'nicolas.employee@vacaciones.local', 'EMPLOYEE', 2, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

SELECT setval('users_id_seq', GREATEST((SELECT COALESCE(MAX(id), 1) FROM users), 1), true);

WITH current_year AS (
    SELECT EXTRACT(YEAR FROM CURRENT_DATE)::INTEGER AS year_value
)
INSERT INTO vacation_balance (
    user_id,
    year,
    total_days,
    used_days,
    available_days,
    created_by,
    updated_by,
    created_at,
    updated_at
)
SELECT
    v.user_id,
    cy.year_value,
    v.total_days,
    v.used_days,
    v.available_days,
    1,
    1,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM current_year cy
CROSS JOIN (
    VALUES
        (2, 20, 5, 15),
        (3, 20, 2, 18),
        (4, 20, 0, 20)
) AS v(user_id, total_days, used_days, available_days)
ON CONFLICT (user_id, year) DO NOTHING;

WITH current_year AS (
    SELECT EXTRACT(YEAR FROM CURRENT_DATE)::INTEGER AS year_value
)
INSERT INTO holiday (date, name, active)
SELECT
    make_date(cy.year_value, h.month_value, h.day_value),
    h.name,
    TRUE
FROM current_year cy
CROSS JOIN (
    VALUES
        (1, 1, 'Anio Nuevo'),
        (5, 1, 'Dia del Trabajador'),
        (12, 25, 'Navidad')
) AS h(month_value, day_value, name)
ON CONFLICT (date) DO NOTHING;

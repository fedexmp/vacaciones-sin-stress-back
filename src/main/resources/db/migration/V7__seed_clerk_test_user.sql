INSERT INTO users (id, clerk_user_id, full_name, email, role, leader_id, active, created_at, updated_at)
VALUES
    (5, NULL, 'Mauro Diale', 'mauro.diale@renaiss.io', 'EMPLOYEE', 2, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (email) DO NOTHING;

SELECT setval('users_id_seq', GREATEST((SELECT COALESCE(MAX(id), 1) FROM users), 1), true);

WITH current_year AS (
    SELECT EXTRACT(YEAR FROM CURRENT_DATE)::INTEGER AS year_value
)
INSERT INTO vacation_balance (user_id, year, total_days, used_days, available_days, created_by, updated_by, created_at, updated_at)
SELECT 5, cy.year_value, 20, 0, 20, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM current_year cy
ON CONFLICT (user_id, year) DO NOTHING;

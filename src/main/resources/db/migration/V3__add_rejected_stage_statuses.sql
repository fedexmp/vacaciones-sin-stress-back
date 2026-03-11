UPDATE vacation_request
SET status = 'REJECTED_HR'
WHERE status = 'REJECTED'
  AND approved_by_hr_id IS NOT NULL;

UPDATE vacation_request
SET status = 'REJECTED_LEADER'
WHERE status = 'REJECTED'
  AND approved_by_hr_id IS NULL
  AND approved_by_leader_id IS NOT NULL;

ALTER TABLE vacation_request
DROP CONSTRAINT IF EXISTS chk_vacation_request_status;

ALTER TABLE vacation_request
ADD CONSTRAINT chk_vacation_request_status
CHECK (status IN (
    'DRAFT',
    'PENDING_LEADER',
    'PENDING_HR',
    'APPROVED',
    'REJECTED_LEADER',
    'REJECTED_HR'
));

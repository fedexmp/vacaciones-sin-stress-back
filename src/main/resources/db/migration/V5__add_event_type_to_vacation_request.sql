ALTER TABLE vacation_request
ADD COLUMN event_type VARCHAR(30) NOT NULL DEFAULT 'VACATION';

ALTER TABLE vacation_request
ADD CONSTRAINT chk_vacation_request_event_type
CHECK (event_type IN (
    'VACATION',
    'LICENCIA'
));

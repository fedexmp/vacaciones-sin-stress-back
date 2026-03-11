ALTER TABLE calendar_event
DROP CONSTRAINT IF EXISTS chk_calendar_event_type;

ALTER TABLE calendar_event
ADD CONSTRAINT chk_calendar_event_type
CHECK (event_type IN (
    'VACATION',
    'LICENCIA'
));

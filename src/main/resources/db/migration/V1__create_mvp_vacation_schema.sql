CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    clerk_user_id VARCHAR(100) UNIQUE,
    full_name VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    role VARCHAR(20) NOT NULL,
    leader_id BIGINT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_users_leader_id FOREIGN KEY (leader_id) REFERENCES users (id),
    CONSTRAINT chk_users_role CHECK (role IN ('EMPLOYEE', 'LEADER', 'HR'))
);

CREATE INDEX idx_users_leader_id ON users (leader_id);
CREATE INDEX idx_users_role ON users (role);

CREATE TABLE vacation_balance (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    year INTEGER NOT NULL,
    total_days INTEGER NOT NULL,
    used_days INTEGER NOT NULL DEFAULT 0,
    available_days INTEGER NOT NULL,
    created_by BIGINT NULL,
    updated_by BIGINT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_vacation_balance_user_id FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_vacation_balance_created_by FOREIGN KEY (created_by) REFERENCES users (id),
    CONSTRAINT fk_vacation_balance_updated_by FOREIGN KEY (updated_by) REFERENCES users (id),
    CONSTRAINT uk_vacation_balance_user_year UNIQUE (user_id, year),
    CONSTRAINT chk_vacation_balance_total_days CHECK (total_days >= 0),
    CONSTRAINT chk_vacation_balance_used_days CHECK (used_days >= 0),
    CONSTRAINT chk_vacation_balance_available_days CHECK (available_days >= 0)
);

CREATE INDEX idx_vacation_balance_user_id ON vacation_balance (user_id);

CREATE TABLE vacation_request (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    request_year INTEGER NOT NULL,
    business_days INTEGER NOT NULL,
    comment VARCHAR(500) NULL,
    status VARCHAR(30) NOT NULL,
    requested_at TIMESTAMP NULL,
    reviewed_by_leader_at TIMESTAMP NULL,
    reviewed_by_hr_at TIMESTAMP NULL,
    approved_by_leader_id BIGINT NULL,
    approved_by_hr_id BIGINT NULL,
    rejection_reason VARCHAR(500) NULL,
    warning_exceeded_ten_days BOOLEAN NOT NULL DEFAULT FALSE,
    warning_retroactive BOOLEAN NOT NULL DEFAULT FALSE,
    validated_with_client BOOLEAN NOT NULL DEFAULT FALSE,
    validated_by BIGINT NULL,
    validated_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_vacation_request_user_id FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_vacation_request_approved_by_leader_id FOREIGN KEY (approved_by_leader_id) REFERENCES users (id),
    CONSTRAINT fk_vacation_request_approved_by_hr_id FOREIGN KEY (approved_by_hr_id) REFERENCES users (id),
    CONSTRAINT fk_vacation_request_validated_by FOREIGN KEY (validated_by) REFERENCES users (id),
    CONSTRAINT chk_vacation_request_status CHECK (status IN ('DRAFT', 'PENDING_LEADER', 'PENDING_HR', 'APPROVED', 'REJECTED')),
    CONSTRAINT chk_vacation_request_range CHECK (start_date <= end_date),
    CONSTRAINT chk_vacation_request_start_year CHECK (EXTRACT(YEAR FROM start_date) = request_year),
    CONSTRAINT chk_vacation_request_end_year CHECK (EXTRACT(YEAR FROM end_date) = request_year)
);

CREATE INDEX idx_vacation_request_user_id ON vacation_request (user_id);
CREATE INDEX idx_vacation_request_status ON vacation_request (status);
CREATE INDEX idx_vacation_request_status_user ON vacation_request (status, user_id);
CREATE INDEX idx_vacation_request_user_dates ON vacation_request (user_id, start_date, end_date);

CREATE TABLE holiday (
    id BIGSERIAL PRIMARY KEY,
    date DATE NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE calendar_event (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    vacation_request_id BIGINT NULL,
    title VARCHAR(200) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    event_type VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_calendar_event_user_id FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_calendar_event_vacation_request_id FOREIGN KEY (vacation_request_id) REFERENCES vacation_request (id),
    CONSTRAINT chk_calendar_event_range CHECK (start_date <= end_date),
    CONSTRAINT chk_calendar_event_type CHECK (event_type IN ('VACATION'))
);

CREATE INDEX idx_calendar_event_user_id ON calendar_event (user_id);
CREATE INDEX idx_calendar_event_vacation_request ON calendar_event (vacation_request_id);

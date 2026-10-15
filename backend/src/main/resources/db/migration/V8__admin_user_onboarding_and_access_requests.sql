ALTER TABLE admin_user ADD COLUMN IF NOT EXISTS must_change_password BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE admin_user ADD COLUMN IF NOT EXISTS password_changed_at TIMESTAMP;

CREATE TABLE admin_access_request (
    id UUID PRIMARY KEY,
    request_type VARCHAR(50) NOT NULL DEFAULT 'ADDITIONAL_AGENCY_ADMIN',
    agency_id UUID NOT NULL,
    requested_role VARCHAR(50) NOT NULL DEFAULT 'AGENCY_ADMIN',
    candidate_first_name VARCHAR(100) NOT NULL,
    candidate_last_name VARCHAR(100) NOT NULL,
    candidate_email VARCHAR(150) NOT NULL,
    candidate_phone VARCHAR(50),
    reason TEXT NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    requested_by UUID NOT NULL,
    reviewed_by UUID,
    reviewed_at TIMESTAMP,
    rejection_reason TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_req_agency FOREIGN KEY (agency_id) REFERENCES agency(id) ON DELETE CASCADE,
    CONSTRAINT fk_req_requester FOREIGN KEY (requested_by) REFERENCES admin_user(id) ON DELETE CASCADE,
    CONSTRAINT fk_req_reviewer FOREIGN KEY (reviewed_by) REFERENCES admin_user(id) ON DELETE SET NULL
);

CREATE INDEX idx_req_agency_status ON admin_access_request(agency_id, status);
CREATE INDEX idx_req_status ON admin_access_request(status);

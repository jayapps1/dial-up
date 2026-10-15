CREATE TABLE user_management_request (
    id UUID PRIMARY KEY,
    request_type VARCHAR(50) NOT NULL DEFAULT 'DEACTIVATE',
    target_user_id UUID NOT NULL,
    agency_id UUID NOT NULL,
    reason TEXT NOT NULL,
    effective_date DATE,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    requested_by UUID NOT NULL,
    reviewed_by UUID,
    reviewed_at TIMESTAMP,
    rejection_reason TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_req_target FOREIGN KEY (target_user_id) REFERENCES admin_user(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_req_agency FOREIGN KEY (agency_id) REFERENCES agency(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_req_requester FOREIGN KEY (requested_by) REFERENCES admin_user(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_req_reviewer FOREIGN KEY (reviewed_by) REFERENCES admin_user(id) ON DELETE SET NULL
);

CREATE INDEX idx_user_req_agency_status ON user_management_request(agency_id, status);
CREATE INDEX idx_user_req_status ON user_management_request(status);

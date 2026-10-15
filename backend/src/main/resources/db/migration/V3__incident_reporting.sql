CREATE TABLE incident_category (
    id UUID PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    default_severity VARCHAR(50) NOT NULL,
    supports_silent BOOLEAN NOT NULL DEFAULT FALSE,
    supports_general BOOLEAN NOT NULL DEFAULT TRUE,
    supports_call BOOLEAN NOT NULL DEFAULT FALSE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE incident_category_agency (
    incident_category_id UUID NOT NULL,
    agency_type VARCHAR(50) NOT NULL,
    PRIMARY KEY (incident_category_id, agency_type),
    CONSTRAINT fk_category_agency_category FOREIGN KEY (incident_category_id) REFERENCES incident_category(id) ON DELETE CASCADE
);

CREATE TABLE incident_report (
    id UUID PRIMARY KEY,
    public_reference VARCHAR(100) NOT NULL UNIQUE,
    category_id UUID NOT NULL,
    report_mode VARCHAR(50) NOT NULL,
    severity VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    description TEXT,
    anonymous_session_id VARCHAR(255),
    tracking_token_hash VARCHAR(255),
    client_generated_request_id UUID UNIQUE,
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    location_accuracy_meters DOUBLE PRECISION,
    landmark VARCHAR(255),
    locality VARCHAR(255),
    district VARCHAR(255),
    region VARCHAR(255),
    vehicle_registration_number VARCHAR(100),
    vehicle_type VARCHAR(100),
    vehicle_colour VARCHAR(100),
    direction_of_travel VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_report_category FOREIGN KEY (category_id) REFERENCES incident_category(id)
);

CREATE TABLE report_assignment (
    id UUID PRIMARY KEY,
    report_id UUID NOT NULL,
    agency_type VARCHAR(50) NOT NULL,
    agency_id UUID,
    station_id UUID,
    status VARCHAR(50) NOT NULL,
    assigned_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_assignment_report FOREIGN KEY (report_id) REFERENCES incident_report(id) ON DELETE CASCADE
);

CREATE TABLE report_status_history (
    id UUID PRIMARY KEY,
    report_id UUID NOT NULL,
    status VARCHAR(50) NOT NULL,
    note TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_history_report FOREIGN KEY (report_id) REFERENCES incident_report(id) ON DELETE CASCADE
);

CREATE INDEX idx_report_reference ON incident_report(public_reference);
CREATE INDEX idx_report_status ON incident_report(status);
CREATE INDEX idx_report_category ON incident_report(category_id);
CREATE INDEX idx_report_created_at ON incident_report(created_at);
CREATE INDEX idx_assignment_report ON report_assignment(report_id, agency_type);
CREATE INDEX idx_history_report ON report_status_history(report_id);

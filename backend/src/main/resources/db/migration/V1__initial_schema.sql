CREATE EXTENSION IF NOT EXISTS postgis;

CREATE TABLE agency (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    code VARCHAR(50) NOT NULL UNIQUE,
    type VARCHAR(50) NOT NULL,
    emergency_number VARCHAR(50),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    verified BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE station (
    id UUID PRIMARY KEY,
    agency_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    code VARCHAR(50) NOT NULL,
    region VARCHAR(100) NOT NULL,
    district VARCHAR(100) NOT NULL,
    locality VARCHAR(100),
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    location_accuracy_meters DOUBLE PRECISION,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    verified BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_station_agency FOREIGN KEY (agency_id) REFERENCES agency(id) ON DELETE CASCADE,
    CONSTRAINT uq_station_code UNIQUE (agency_id, code)
);

CREATE TABLE station_contact (
    id UUID PRIMARY KEY,
    station_id UUID NOT NULL,
    phone_number VARCHAR(50) NOT NULL,
    contact_type VARCHAR(50) NOT NULL,
    priority INT NOT NULL DEFAULT 1,
    public_contact BOOLEAN NOT NULL DEFAULT TRUE,
    available_24_hour BOOLEAN NOT NULL DEFAULT FALSE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    verified BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_contact_station FOREIGN KEY (station_id) REFERENCES station(id) ON DELETE CASCADE
);

CREATE INDEX idx_agency_active_verified ON agency(active, verified);
CREATE INDEX idx_station_agency_active_verified ON station(agency_id, active, verified);
CREATE INDEX idx_station_region_district ON station(region, district);
CREATE INDEX idx_contact_station_priority ON station_contact(station_id, active, verified, priority);

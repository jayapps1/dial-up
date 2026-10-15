-- LOCAL DEVELOPMENT SEED INCIDENT CATEGORIES ONLY. NOT FOR PRODUCTION ROUTING.

INSERT INTO incident_category (id, code, name, description, default_severity, supports_silent, supports_general, supports_call, active, created_at, updated_at) VALUES
('b1111111-1111-1111-1111-111111111111', 'FIRE', 'Fire & Explosion', 'Structural fires, wildfire, chemical or gas leak hazards', 'HIGH', TRUE, TRUE, TRUE, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('b2222222-2222-2222-2222-222222222222', 'ROBBERY', 'Armed Robbery / Assault', 'Active violent crime or imminent physical threat', 'CRITICAL', TRUE, TRUE, TRUE, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('b3333333-3333-3333-3333-333333333333', 'MEDICAL', 'Medical Emergency', 'Severe injury, collapse, cardiac or respiratory distress', 'HIGH', TRUE, TRUE, TRUE, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('b4444444-4444-4444-4444-444444444444', 'ACCIDENT', 'Road Traffic Accident', 'Vehicle collisions with injuries or blocked roadway', 'HIGH', TRUE, TRUE, TRUE, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('b5555555-5555-5555-5555-555555555555', 'SUSPICIOUS', 'Suspicious Activity', 'Unattended items, suspicious persons, or potential threats', 'MEDIUM', TRUE, TRUE, TRUE, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('b6666666-6666-6666-6666-666666666666', 'HAZARD', 'Public Safety Hazard', 'Flooding, fallen power lines, or structural collapse', 'MEDIUM', TRUE, TRUE, TRUE, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('b7777777-7777-7777-7777-777777777777', 'RECKLESS_DRIVING', 'Dangerous Driving', 'Speeding, reckless driving, or hit-and-run incidents', 'MEDIUM', FALSE, TRUE, TRUE, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO incident_category_agency (incident_category_id, agency_type) VALUES
('b1111111-1111-1111-1111-111111111111', 'FIRE'),
('b2222222-2222-2222-2222-222222222222', 'POLICE'),
('b3333333-3333-3333-3333-333333333333', 'AMBULANCE'),
('b4444444-4444-4444-4444-444444444444', 'POLICE'),
('b4444444-4444-4444-4444-444444444444', 'AMBULANCE'),
('b4444444-4444-4444-4444-444444444444', 'FIRE'),
('b5555555-5555-5555-5555-555555555555', 'POLICE'),
('b6666666-6666-6666-6666-666666666666', 'DISASTER_MANAGEMENT'),
('b6666666-6666-6666-6666-666666666666', 'FIRE'),
('b7777777-7777-7777-7777-777777777777', 'POLICE');

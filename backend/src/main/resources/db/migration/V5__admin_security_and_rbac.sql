-- ============================================================
-- V5__admin_security_and_rbac.sql
-- Ghana Emergency Connect
-- Admin security, RBAC, refresh tokens, audit logging
-- ============================================================


-- ============================================================
-- ADMIN USERS
-- ============================================================

CREATE TABLE admin_user (
                            id UUID PRIMARY KEY,
                            email VARCHAR(255) NOT NULL UNIQUE,
                            password_hash VARCHAR(255) NOT NULL,
                            first_name VARCHAR(100) NOT NULL,
                            last_name VARCHAR(100) NOT NULL,
                            phone_number VARCHAR(50),

                            agency_id UUID,
                            region VARCHAR(100),
                            station_id UUID,

                            active BOOLEAN NOT NULL DEFAULT TRUE,
                            locked BOOLEAN NOT NULL DEFAULT FALSE,
                            failed_login_attempts INT NOT NULL DEFAULT 0,
                            must_change_password BOOLEAN NOT NULL DEFAULT FALSE,

                            last_login_at TIMESTAMP,
                            password_changed_at TIMESTAMP,

                            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            created_by UUID,

                            CONSTRAINT fk_admin_user_agency
                                FOREIGN KEY (agency_id)
                                    REFERENCES agency(id)
                                    ON DELETE SET NULL,

                            CONSTRAINT fk_admin_user_station
                                FOREIGN KEY (station_id)
                                    REFERENCES station(id)
                                    ON DELETE SET NULL
);


-- ============================================================
-- ROLES
-- ============================================================

CREATE TABLE role (
                      id UUID PRIMARY KEY,
                      code VARCHAR(50) NOT NULL UNIQUE,
                      name VARCHAR(100) NOT NULL,
                      description VARCHAR(255),
                      system_role BOOLEAN NOT NULL DEFAULT TRUE,
                      created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);


-- ============================================================
-- PERMISSIONS
-- ============================================================

CREATE TABLE permission (
                            id UUID PRIMARY KEY,
                            code VARCHAR(100) NOT NULL UNIQUE,
                            name VARCHAR(100) NOT NULL,
                            description VARCHAR(255),
                            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);


-- ============================================================
-- USER <-> ROLE
-- ============================================================

CREATE TABLE user_role (
                           user_id UUID NOT NULL,
                           role_id UUID NOT NULL,

                           PRIMARY KEY (user_id, role_id),

                           CONSTRAINT fk_user_role_user
                               FOREIGN KEY (user_id)
                                   REFERENCES admin_user(id)
                                   ON DELETE CASCADE,

                           CONSTRAINT fk_user_role_role
                               FOREIGN KEY (role_id)
                                   REFERENCES role(id)
                                   ON DELETE CASCADE
);


-- ============================================================
-- ROLE <-> PERMISSION
-- ============================================================

CREATE TABLE role_permission (
                                 role_id UUID NOT NULL,
                                 permission_id UUID NOT NULL,

                                 PRIMARY KEY (role_id, permission_id),

                                 CONSTRAINT fk_role_permission_role
                                     FOREIGN KEY (role_id)
                                         REFERENCES role(id)
                                         ON DELETE CASCADE,

                                 CONSTRAINT fk_role_permission_permission
                                     FOREIGN KEY (permission_id)
                                         REFERENCES permission(id)
                                         ON DELETE CASCADE
);


-- ============================================================
-- ADMIN REFRESH TOKENS
-- ============================================================

CREATE TABLE admin_refresh_token (
                                     id UUID PRIMARY KEY,
                                     user_id UUID NOT NULL,
                                     token_hash VARCHAR(255) NOT NULL,
                                     expires_at TIMESTAMP NOT NULL,
                                     revoked_at TIMESTAMP,
                                     created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                     last_used_at TIMESTAMP,

                                     CONSTRAINT fk_refresh_token_user
                                         FOREIGN KEY (user_id)
                                             REFERENCES admin_user(id)
                                             ON DELETE CASCADE
);


-- ============================================================
-- AUDIT LOG
-- ============================================================

CREATE TABLE audit_log (
                           id UUID PRIMARY KEY,
                           actor_user_id UUID,
                           action VARCHAR(100) NOT NULL,
                           entity_type VARCHAR(100) NOT NULL,
                           entity_id UUID,
                           agency_id UUID,
                           description TEXT,
                           ip_address VARCHAR(100),
                           user_agent VARCHAR(255),
                           created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);


-- ============================================================
-- INDEXES
-- ============================================================

CREATE INDEX idx_admin_user_email
    ON admin_user(email);

CREATE INDEX idx_admin_user_agency
    ON admin_user(agency_id);

CREATE INDEX idx_refresh_token_user
    ON admin_refresh_token(user_id);

CREATE INDEX idx_refresh_token_hash
    ON admin_refresh_token(token_hash);

CREATE INDEX idx_audit_log_actor
    ON audit_log(actor_user_id);

CREATE INDEX idx_audit_log_agency
    ON audit_log(agency_id);

CREATE INDEX idx_audit_log_action
    ON audit_log(action);


-- ============================================================
-- SEED SYSTEM ROLES
-- ============================================================

INSERT INTO role (
    id,
    code,
    name,
    description,
    system_role,
    created_at
)
VALUES
    (
        '11111111-1111-1111-1111-111111111111',
        'SYSTEM_ADMIN',
        'System Administrator',
        'Full platform-wide administrative access',
        TRUE,
        CURRENT_TIMESTAMP
    ),
    (
        '22222222-2222-2222-2222-222222222222',
        'AGENCY_ADMIN',
        'Agency Administrator',
        'Full management access within an assigned agency',
        TRUE,
        CURRENT_TIMESTAMP
    ),
    (
        '33333333-3333-3333-3333-333333333333',
        'REGIONAL_ADMIN',
        'Regional Administrator',
        'Management access within an assigned region and agency',
        TRUE,
        CURRENT_TIMESTAMP
    ),
    (
        '44444444-4444-4444-4444-444444444444',
        'STATION_ADMIN',
        'Station Administrator',
        'Management access within an assigned station',
        TRUE,
        CURRENT_TIMESTAMP
    ),
    (
        '55555555-5555-5555-5555-555555555555',
        'CONTROL_ROOM_OPERATOR',
        'Control Room Operator',
        'Operational report handling, assignment, and status updates',
        TRUE,
        CURRENT_TIMESTAMP
    ),
    (
        '66666666-6666-6666-6666-666666666666',
        'REPORT_REVIEWER',
        'Report Reviewer',
        'Review and classify incoming incident reports',
        TRUE,
        CURRENT_TIMESTAMP
    ),
    (
        '77777777-7777-7777-7777-777777777777',
        'AUDITOR',
        'System Auditor',
        'Read-only audit and compliance log access',
        TRUE,
        CURRENT_TIMESTAMP
    );


-- ============================================================
-- SEED PERMISSIONS
-- ============================================================

INSERT INTO permission (
    id,
    code,
    name,
    description,
    created_at
)
VALUES

-- Agency permissions
(
    '01010101-1111-1111-1111-111111111111',
    'AGENCY_READ',
    'Read Agencies',
    'View agency details',
    CURRENT_TIMESTAMP
),
(
    '01010102-1111-1111-1111-111111111111',
    'AGENCY_CREATE',
    'Create Agency',
    'Create new agency records',
    CURRENT_TIMESTAMP
),
(
    '01010103-1111-1111-1111-111111111111',
    'AGENCY_UPDATE',
    'Update Agency',
    'Modify agency details',
    CURRENT_TIMESTAMP
),
(
    '01010104-1111-1111-1111-111111111111',
    'AGENCY_VERIFY',
    'Verify Agency',
    'Verify official agency records',
    CURRENT_TIMESTAMP
),

-- Station permissions
(
    '02010101-1111-1111-1111-111111111111',
    'STATION_READ',
    'Read Stations',
    'View station details',
    CURRENT_TIMESTAMP
),
(
    '02010102-1111-1111-1111-111111111111',
    'STATION_CREATE',
    'Create Station',
    'Create new station records',
    CURRENT_TIMESTAMP
),
(
    '02010103-1111-1111-1111-111111111111',
    'STATION_UPDATE',
    'Update Station',
    'Modify station details',
    CURRENT_TIMESTAMP
),
(
    '02010104-1111-1111-1111-111111111111',
    'STATION_VERIFY',
    'Verify Station',
    'Verify station records',
    CURRENT_TIMESTAMP
),

-- Station contact permissions
(
    '03010101-1111-1111-1111-111111111111',
    'STATION_CONTACT_READ',
    'Read Station Contacts',
    'View station contacts',
    CURRENT_TIMESTAMP
),
(
    '03010102-1111-1111-1111-111111111111',
    'STATION_CONTACT_CREATE',
    'Create Station Contact',
    'Add contacts to stations',
    CURRENT_TIMESTAMP
),
(
    '03010103-1111-1111-1111-111111111111',
    'STATION_CONTACT_UPDATE',
    'Update Station Contact',
    'Modify station contacts',
    CURRENT_TIMESTAMP
),
(
    '03010104-1111-1111-1111-111111111111',
    'STATION_CONTACT_VERIFY',
    'Verify Station Contact',
    'Verify station contacts',
    CURRENT_TIMESTAMP
),

-- Report permissions
(
    '04010101-1111-1111-1111-111111111111',
    'REPORT_READ',
    'Read Incident Reports',
    'View reports within scope',
    CURRENT_TIMESTAMP
),
(
    '04010102-1111-1111-1111-111111111111',
    'REPORT_ACKNOWLEDGE',
    'Acknowledge Report',
    'Acknowledge incoming reports',
    CURRENT_TIMESTAMP
),
(
    '04010103-1111-1111-1111-111111111111',
    'REPORT_ASSIGN',
    'Assign Report',
    'Assign report to station or responder',
    CURRENT_TIMESTAMP
),
(
    '04010104-1111-1111-1111-111111111111',
    'REPORT_UPDATE_STATUS',
    'Update Report Status',
    'Update report status lifecycle',
    CURRENT_TIMESTAMP
),
(
    '04010105-1111-1111-1111-111111111111',
    'REPORT_TRANSFER',
    'Transfer Report',
    'Transfer report to another agency',
    CURRENT_TIMESTAMP
),
(
    '04010106-1111-1111-1111-111111111111',
    'REPORT_CLOSE',
    'Close Report',
    'Close or resolve incident reports',
    CURRENT_TIMESTAMP
),

-- Incident category permissions
(
    '05010101-1111-1111-1111-111111111111',
    'INCIDENT_CATEGORY_READ',
    'Read Categories',
    'View incident categories',
    CURRENT_TIMESTAMP
),
(
    '05010102-1111-1111-1111-111111111111',
    'INCIDENT_CATEGORY_MANAGE',
    'Manage Categories',
    'Manage incident categories',
    CURRENT_TIMESTAMP
),

-- Admin user permissions
(
    '06010101-1111-1111-1111-111111111111',
    'ADMIN_USER_READ',
    'Read Admin Users',
    'View admin user accounts',
    CURRENT_TIMESTAMP
),
(
    '06010102-1111-1111-1111-111111111111',
    'ADMIN_USER_CREATE',
    'Create Admin User',
    'Create admin user accounts',
    CURRENT_TIMESTAMP
),
(
    '06010103-1111-1111-1111-111111111111',
    'ADMIN_USER_UPDATE',
    'Update Admin User',
    'Modify admin user accounts',
    CURRENT_TIMESTAMP
),

-- Role permissions
(
    '07010101-1111-1111-1111-111111111111',
    'ROLE_READ',
    'Read Roles',
    'View roles and permissions',
    CURRENT_TIMESTAMP
),
(
    '07010102-1111-1111-1111-111111111111',
    'ROLE_MANAGE',
    'Manage Roles',
    'Modify roles and permission mappings',
    CURRENT_TIMESTAMP
),

-- Audit permissions
(
    '08010101-1111-1111-1111-111111111111',
    'AUDIT_LOG_READ',
    'Read Audit Logs',
    'View security and action audit logs',
    CURRENT_TIMESTAMP
),

-- System settings
(
    '09010101-1111-1111-1111-111111111111',
    'SYSTEM_SETTINGS_MANAGE',
    'Manage System Settings',
    'Manage platform system settings',
    CURRENT_TIMESTAMP
);


-- ============================================================
-- SYSTEM ADMIN
-- Give SYSTEM_ADMIN every permission
-- ============================================================

INSERT INTO role_permission (
    role_id,
    permission_id
)
SELECT
    '11111111-1111-1111-1111-111111111111',
    id
FROM permission;


-- ============================================================
-- AGENCY ADMIN PERMISSIONS
-- ============================================================

INSERT INTO role_permission (
    role_id,
    permission_id
)
VALUES

    (
        '22222222-2222-2222-2222-222222222222',
        '01010101-1111-1111-1111-111111111111'
    ),
    (
        '22222222-2222-2222-2222-222222222222',
        '01010103-1111-1111-1111-111111111111'
    ),
    (
        '22222222-2222-2222-2222-222222222222',
        '02010101-1111-1111-1111-111111111111'
    ),
    (
        '22222222-2222-2222-2222-222222222222',
        '02010102-1111-1111-1111-111111111111'
    ),
    (
        '22222222-2222-2222-2222-222222222222',
        '02010103-1111-1111-1111-111111111111'
    ),
    (
        '22222222-2222-2222-2222-222222222222',
        '03010101-1111-1111-1111-111111111111'
    ),
    (
        '22222222-2222-2222-2222-222222222222',
        '03010102-1111-1111-1111-111111111111'
    ),
    (
        '22222222-2222-2222-2222-222222222222',
        '03010103-1111-1111-1111-111111111111'
    ),
    (
        '22222222-2222-2222-2222-222222222222',
        '04010101-1111-1111-1111-111111111111'
    ),
    (
        '22222222-2222-2222-2222-222222222222',
        '04010102-1111-1111-1111-111111111111'
    ),
    (
        '22222222-2222-2222-2222-222222222222',
        '04010103-1111-1111-1111-111111111111'
    ),
    (
        '22222222-2222-2222-2222-222222222222',
        '04010104-1111-1111-1111-111111111111'
    ),
    (
        '22222222-2222-2222-2222-222222222222',
        '04010105-1111-1111-1111-111111111111'
    ),
    (
        '22222222-2222-2222-2222-222222222222',
        '04010106-1111-1111-1111-111111111111'
    ),
    (
        '22222222-2222-2222-2222-222222222222',
        '06010101-1111-1111-1111-111111111111'
    ),
    (
        '22222222-2222-2222-2222-222222222222',
        '06010102-1111-1111-1111-111111111111'
    ),
    (
        '22222222-2222-2222-2222-222222222222',
        '06010103-1111-1111-1111-111111111111'
    );


-- ============================================================
-- CONTROL ROOM OPERATOR PERMISSIONS
-- ============================================================

INSERT INTO role_permission (
    role_id,
    permission_id
)
VALUES

    (
        '55555555-5555-5555-5555-555555555555',
        '02010101-1111-1111-1111-111111111111'
    ),
    (
        '55555555-5555-5555-5555-555555555555',
        '03010101-1111-1111-1111-111111111111'
    ),
    (
        '55555555-5555-5555-5555-555555555555',
        '04010101-1111-1111-1111-111111111111'
    ),
    (
        '55555555-5555-5555-5555-555555555555',
        '04010102-1111-1111-1111-111111111111'
    ),
    (
        '55555555-5555-5555-5555-555555555555',
        '04010103-1111-1111-1111-111111111111'
    ),
    (
        '55555555-5555-5555-5555-555555555555',
        '04010104-1111-1111-1111-111111111111'
    );


-- ============================================================
-- AUDITOR PERMISSIONS
-- ============================================================

INSERT INTO role_permission (
    role_id,
    permission_id
)
VALUES

    (
        '77777777-7777-7777-7777-777777777777',
        '01010101-1111-1111-1111-111111111111'
    ),
    (
        '77777777-7777-7777-7777-777777777777',
        '02010101-1111-1111-1111-111111111111'
    ),
    (
        '77777777-7777-7777-7777-777777777777',
        '03010101-1111-1111-1111-111111111111'
    ),
    (
        '77777777-7777-7777-7777-777777777777',
        '04010101-1111-1111-1111-111111111111'
    ),
    (
        '77777777-7777-7777-7777-777777777777',
        '08010101-1111-1111-1111-111111111111'
    );
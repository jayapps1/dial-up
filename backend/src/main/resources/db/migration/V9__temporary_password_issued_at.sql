ALTER TABLE admin_user ADD COLUMN IF NOT EXISTS temporary_password_issued_at TIMESTAMP;

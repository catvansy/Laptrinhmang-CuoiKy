-- Migration script to add avatar_url and chat_theme columns to users table
-- Run this script if your database doesn't auto-update via Hibernate

ALTER TABLE users 
ADD COLUMN IF NOT EXISTS avatar_url VARCHAR(255) NULL,
ADD COLUMN IF NOT EXISTS chat_theme VARCHAR(50) NULL DEFAULT 'default';

-- Note: IF NOT EXISTS syntax may not work in all MySQL versions
-- If you get an error, remove "IF NOT EXISTS" and run:
-- ALTER TABLE users ADD COLUMN avatar_url VARCHAR(255) NULL;
-- ALTER TABLE users ADD COLUMN chat_theme VARCHAR(50) NULL DEFAULT 'default';



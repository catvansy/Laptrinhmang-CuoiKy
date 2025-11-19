-- Migration: Add edited_at and is_deleted columns to messages table
-- Run this SQL script to add support for message editing and deletion

ALTER TABLE messages
ADD COLUMN edited_at DATETIME DEFAULT NULL,
ADD COLUMN is_deleted BOOLEAN DEFAULT FALSE;

-- Update existing messages to have is_deleted = false
UPDATE messages SET is_deleted = FALSE WHERE is_deleted IS NULL;


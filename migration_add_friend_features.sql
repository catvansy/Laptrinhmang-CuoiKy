-- Migration: Add nickname and blocked fields to friendships table
USE megachat;

ALTER TABLE friendships 
ADD COLUMN nickname VARCHAR(100) NULL,
ADD COLUMN blocked BOOLEAN NOT NULL DEFAULT FALSE;


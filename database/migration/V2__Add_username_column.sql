-- Add username column to users table
ALTER TABLE users ADD username NVARCHAR(50) NULL UNIQUE;
GO

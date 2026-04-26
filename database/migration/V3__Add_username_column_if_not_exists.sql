-- Add username column to users table if not exists
IF NOT EXISTS (
    SELECT * FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE TABLE_NAME = 'users' AND COLUMN_NAME = 'username'
)
BEGIN
    ALTER TABLE users ADD username NVARCHAR(50) NULL;
    ALTER TABLE users ADD CONSTRAINT UQ_Username UNIQUE (username);
END;
GO

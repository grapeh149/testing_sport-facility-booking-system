-- Insert test data for ADMIN, OWNER, and CUSTOMER users
-- Password: Password123!
-- These are pre-hashed with BCrypt

SET IDENTITY_INSERT users ON;

-- Clear existing test data if any
DELETE FROM users WHERE id IN (1,2,3,4,5,6);

INSERT INTO users (id, full_name, email, password_hash, phone, role, is_active, avatar_url, created_at, updated_at)
VALUES
    -- ADMIN Account
    (1, N'Admin Hệ Thống', 'admin@sportbook.vn', 
     '$2a$10$gPBmavPCMFH2T6h1QqOFx.EQU/SVWn6OQYphgrDIw1iBx9b.2pN3C',
     '0800000001', 'ADMIN', 1, NULL, GETDATE(), GETDATE()),
    
    -- OWNER Accounts
    (2, N'Lê Hoàng Minh', 'minh.le@gmail.com',
     '$2a$10$gPBmavPCMFH2T6h1QqOFx.EQU/SVWn6OQYphgrDIw1iBx9b.2pN3C',
     '0923456789', 'OWNER', 1, NULL, GETDATE(), GETDATE()),
    
    (3, N'Phạm Quốc Dũng', 'dung.pham@gmail.com',
     '$2a$10$gPBmavPCMFH2T6h1QqOFx.EQU/SVWn6OQYphgrDIw1iBx9b.2pN3C',
     '0934567890', 'OWNER', 1, NULL, GETDATE(), GETDATE()),
    
    -- CUSTOMER Accounts
    (4, N'Nguyễn Văn An', 'an.nguyen@gmail.com',
     '$2a$10$gPBmavPCMFH2T6h1QqOFx.EQU/SVWn6OQYphgrDIw1iBx9b.2pN3C',
     '0901234567', 'CUSTOMER', 1, NULL, GETDATE(), GETDATE()),
    
    (5, N'Trần Thị Bình', 'binh.tran@gmail.com',
     '$2a$10$gPBmavPCMFH2T6h1QqOFx.EQU/SVWn6OQYphgrDIw1iBx9b.2pN3C',
     '0912345678', 'CUSTOMER', 1, NULL, GETDATE(), GETDATE()),
    
    (6, N'Vũ Thành Long', 'long.vu@gmail.com',
     '$2a$10$gPBmavPCMFH2T6h1QqOFx.EQU/SVWn6OQYphgrDIw1iBx9b.2pN3C',
     '0945678901', 'CUSTOMER', 0, NULL, GETDATE(), GETDATE());

SET IDENTITY_INSERT users OFF;
GO

-- Update NotificationType CHECK constraint to include all new enum values
-- Drop old constraint
IF EXISTS (
    SELECT * FROM INFORMATION_SCHEMA.CONSTRAINT_COLUMN_USAGE
    WHERE CONSTRAINT_NAME = 'CK_notifications_type'
)
BEGIN
    ALTER TABLE notifications DROP CONSTRAINT CK_notifications_type;
END;

-- Create new constraint with all enum values
ALTER TABLE notifications
ADD CONSTRAINT CK_notifications_type CHECK (
    [type] IN (
        'BOOKING_CREATED',
        'BOOKING_PAYMENT_SUCCESS',
        'BOOKING_PAYMENT_FAILED',
        'BOOKING_CONFIRMED',
        'BOOKING_CANCELLED',
        'CHECK_IN',
        'REVIEW_REPLY',
        'FACILITY_APPROVED',
        'FACILITY_REJECTED',
        'FACILITY_CANCELLED_BY_ADMIN',
        'REFUND_PROCESSED'
    )
);

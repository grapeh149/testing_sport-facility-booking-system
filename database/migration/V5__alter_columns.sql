-- For booking_code
ALTER TABLE bookings DROP CONSTRAINT UQ_bookings_code;
ALTER TABLE bookings ALTER COLUMN booking_code VARCHAR(20);
ALTER TABLE bookings ADD CONSTRAINT UQ_bookings_code UNIQUE (booking_code);

-- For commission_amount
ALTER TABLE bookings DROP CONSTRAINT DF__bookings__commis__0E6E26BF;
ALTER TABLE bookings ALTER COLUMN commission_amount NUMERIC(38,2);
ALTER TABLE bookings ADD CONSTRAINT DF__bookings__commis__0E6E26BF DEFAULT 0 FOR commission_amount;

-- For deposit_amount
ALTER TABLE bookings DROP CONSTRAINT CK_bookings_deposit;
ALTER TABLE bookings ALTER COLUMN deposit_amount NUMERIC(38,2);
ALTER TABLE bookings ADD CONSTRAINT CK_bookings_deposit CHECK (deposit_amount >= 0);

-- For total_price
ALTER TABLE bookings DROP CONSTRAINT CK_bookings_price;
ALTER TABLE bookings ALTER COLUMN total_price NUMERIC(38,2);
ALTER TABLE bookings ADD CONSTRAINT CK_bookings_price CHECK (total_price > 0);

-- For payments amount
ALTER TABLE payments DROP CONSTRAINT CK_payments_amount;
ALTER TABLE payments ALTER COLUMN amount NUMERIC(38,2);
ALTER TABLE payments ADD CONSTRAINT CK_payments_amount CHECK (amount > 0);

-- For reviews rating
ALTER TABLE reviews DROP CONSTRAINT CK_reviews_rating;
ALTER TABLE reviews ALTER COLUMN rating INT;
ALTER TABLE reviews ADD CONSTRAINT CK_reviews_rating CHECK (rating >= 1 AND rating <= 5);

-- For users email
ALTER TABLE users DROP CONSTRAINT UK6dotkott2kjsp8vw4d0m25fb7;
ALTER TABLE users ALTER COLUMN email VARCHAR(150);
ALTER TABLE users ADD CONSTRAINT UK6dotkott2kjsp8vw4d0m25fb7 UNIQUE (email);
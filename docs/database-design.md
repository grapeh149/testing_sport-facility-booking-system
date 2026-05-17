# Thiết Kế Database

## Hệ Thống Đặt Sân Thể Thao — Sport Facility Booking

**Database:** sportfacilitybooking_db  
**Engine:** SQL Server / Azure SQL  
**Charset:** NVARCHAR (Unicode support)  

---

## 1. SƠ ĐỒ QUAN HỆ

```
users (ADMIN) ────────────────────────────┐
                                          │
users (OWNER) ──→ facilities ──→ courts ──┤
                      │             │     │
                      ↓             ↓     │
              facility_approval   time_   │
                  _logs           slots   │
                      ↑             │     │
              users (ADMIN)         ↓     ↓
                               bookings ←─┘
                                   │
                    ┌──────────────┼──────────────┐
                    ↓              ↓              ↓
                payments       check_ins       reviews
                    
notifications ──→ users

facility_images ──→ facilities
sport_types ──→ courts
```

---

## 2. CHI TIẾT TỪNG BẢNG

---

### 2.1 users

Lưu thông tin tất cả người dùng hệ thống (Admin, Owner, Customer).

| Cột           | Kiểu          | Ràng buộc                      | Mô tả                 |
| ------------- | ------------- | ------------------------------ | --------------------- |
| id            | BIGINT        | PK, IDENTITY                   | Mã người dùng         |
| full_name     | NVARCHAR(100) | NOT NULL                       | Họ và tên             |
| email         | VARCHAR(255)  | UNIQUE, CHECK (`%@%.%`)        | Email đăng nhập       |
| password_hash | VARCHAR(255)  | NOT NULL                       | Mật khẩu đã mã hóa   |
| phone         | VARCHAR(15)   | CHECK (len 9–15)               | Số điện thoại         |
| role          | VARCHAR(255)  | CHECK (ADMIN/OWNER/CUSTOMER)   | Vai trò người dùng    |
| is_active     | BIT           | DEFAULT 1                      | Trạng thái hoạt động  |
| avatar_url    | VARCHAR(500)  |                                | Ảnh đại diện          |
| address       | NVARCHAR(MAX) |                                | Địa chỉ               |
| username      | VARCHAR(50)   |                                | Tên đăng nhập         |
| created_at    | DATETIME      | DEFAULT GETDATE()              | Ngày tạo              |
| updated_at    | DATETIME      |                                | Ngày cập nhật         |

```sql
CREATE TABLE [dbo].[users](
    [id] [bigint] IDENTITY(1,1) NOT NULL,
    [full_name] [nvarchar](100) NOT NULL,
    [email] [varchar](255) NULL,
    [password_hash] [varchar](255) NOT NULL,
    [phone] [varchar](15) NULL,
    [role] [varchar](255) NULL,
    [is_active] [bit] NOT NULL,
    [avatar_url] [varchar](500) NULL,
    [created_at] [datetime] NOT NULL,
    [updated_at] [datetime] NULL,
    [address] [nvarchar](max) NULL,
    [username] [varchar](50) NULL,
    CONSTRAINT [PK_users] PRIMARY KEY CLUSTERED ([id] ASC)
);
```

---

### 2.2 sport_types

Danh mục các loại hình thể thao.

| Cột        | Kiểu          | Ràng buộc     | Mô tả                |
| ---------- | ------------- | ------------- | -------------------- |
| id         | INT           | PK, IDENTITY  | Mã loại thể thao     |
| name       | NVARCHAR(100) | NOT NULL, UNIQUE | Tên loại thể thao |
| description| VARCHAR(500)  |               | Mô tả                |
| icon_url   | VARCHAR(500)  |               | URL icon             |
| is_active  | BIT           | DEFAULT 1     | Đang hoạt động       |
| created_at | DATETIME      | DEFAULT GETDATE() | Ngày tạo         |

```sql
CREATE TABLE [dbo].[sport_types](
    [id] [int] IDENTITY(1,1) NOT NULL,
    [name] [nvarchar](100) NOT NULL,
    [description] [varchar](500) NULL,
    [icon_url] [varchar](500) NULL,
    [is_active] [bit] NOT NULL,
    [created_at] [datetime] NOT NULL,
    CONSTRAINT [PK_sport_types] PRIMARY KEY CLUSTERED ([id] ASC)
);
```

---

### 2.3 facilities

Thông tin cơ sở thể thao do Owner quản lý.

| Cột                 | Kiểu          | Ràng buộc                           | Mô tả                        |
| ------------------- | ------------- | ----------------------------------- | ---------------------------- |
| id                  | BIGINT        | PK, IDENTITY                        | Mã cơ sở                     |
| owner_id            | BIGINT        | FK → users                          | Chủ cơ sở                    |
| name                | VARCHAR(200)  |                                     | Tên cơ sở                    |
| address             | VARCHAR(500)  |                                     | Địa chỉ                      |
| district            | NVARCHAR(100) | NOT NULL                            | Quận/Huyện                   |
| city                | NVARCHAR(100) | NOT NULL                            | Thành phố                    |
| latitude            | FLOAT         |                                     | Vĩ độ                        |
| longitude           | FLOAT         |                                     | Kinh độ                      |
| phone               | VARCHAR(15)   |                                     | Số điện thoại                |
| description         | VARCHAR(1000) |                                     | Mô tả cơ sở                  |
| open_time           | TIME          | NOT NULL                            | Giờ mở cửa                   |
| close_time          | TIME          | NOT NULL, CHECK (> open_time)       | Giờ đóng cửa                 |
| status              | VARCHAR(255)  | DEFAULT 'PENDING', CHECK (PENDING/APPROVED/REJECTED/SUSPENDED) | Trạng thái duyệt |
| commission_rate     | DECIMAL(5,2)  | DEFAULT 5.00, CHECK (0–100)         | Tỷ lệ hoa hồng (%)           |
| cancel_before_hours | INT           | DEFAULT 24, CHECK (≥ 0)             | Giờ hủy trước tối thiểu      |
| auto_confirm        | BIT           | DEFAULT 0                           | Tự động xác nhận booking     |
| avg_rating          | DECIMAL(3,2)  | DEFAULT 0.00, CHECK (0–5)           | Điểm đánh giá trung bình     |
| total_reviews       | INT           | DEFAULT 0                           | Tổng số đánh giá             |
| cover_image_url     | VARCHAR(500)  |                                     | Ảnh bìa cơ sở                |
| created_at          | DATETIME      | DEFAULT GETDATE()                   | Ngày tạo                     |
| updated_at          | DATETIME      |                                     | Ngày cập nhật                |

```sql
CREATE TABLE [dbo].[facilities](
    [id] [bigint] IDENTITY(1,1) NOT NULL,
    [owner_id] [bigint] NOT NULL,
    [name] [varchar](200) NULL,
    [address] [varchar](500) NULL,
    [district] [nvarchar](100) NOT NULL,
    [city] [nvarchar](100) NOT NULL,
    [latitude] [float] NULL,
    [longitude] [float] NULL,
    [phone] [varchar](15) NULL,
    [description] [varchar](1000) NULL,
    [open_time] [time](7) NOT NULL,
    [close_time] [time](7) NOT NULL,
    [status] [varchar](255) NULL,
    [commission_rate] [decimal](5, 2) NOT NULL,
    [cancel_before_hours] [int] NOT NULL,
    [auto_confirm] [bit] NOT NULL,
    [avg_rating] [decimal](3, 2) NULL,
    [total_reviews] [int] NOT NULL,
    [cover_image_url] [varchar](500) NULL,
    [created_at] [datetime] NOT NULL,
    [updated_at] [datetime] NULL,
    CONSTRAINT [PK_facilities] PRIMARY KEY CLUSTERED ([id] ASC),
    FOREIGN KEY ([owner_id]) REFERENCES [users]([id])
);
```

---

### 2.4 facility_images

Hình ảnh minh họa cho cơ sở thể thao.

| Cột        | Kiểu          | Ràng buộc     | Mô tả              |
| ---------- | ------------- | ------------- | ------------------ |
| id         | BIGINT        | PK, IDENTITY  | Mã ảnh             |
| facility_id| BIGINT        | FK → facilities | Cơ sở liên quan  |
| image_url  | VARCHAR(500)  |               | URL hình ảnh       |
| is_primary | BIT           | DEFAULT 0     | Ảnh đại diện       |
| sort_order | INT           | DEFAULT 0     | Thứ tự hiển thị    |
| caption    | NVARCHAR(MAX) |               | Chú thích ảnh      |
| created_at | DATETIME      | DEFAULT GETDATE() | Ngày tạo       |

```sql
CREATE TABLE [dbo].[facility_images](
    [id] [bigint] IDENTITY(1,1) NOT NULL,
    [facility_id] [bigint] NOT NULL,
    [image_url] [varchar](500) NULL,
    [is_primary] [bit] NOT NULL,
    [sort_order] [int] NOT NULL,
    [created_at] [datetime] NOT NULL,
    [caption] [nvarchar](max) NULL,
    CONSTRAINT [PK_facility_images] PRIMARY KEY CLUSTERED ([id] ASC),
    FOREIGN KEY ([facility_id]) REFERENCES [facilities]([id])
);
```

---

### 2.5 facility_approval_logs

Lịch sử duyệt/từ chối cơ sở bởi Admin.

| Cột         | Kiểu         | Ràng buộc                                    | Mô tả              |
| ----------- | ------------ | -------------------------------------------- | ------------------ |
| id          | BIGINT       | PK, IDENTITY                                 | Mã log             |
| facility_id | BIGINT       | FK → facilities                              | Cơ sở liên quan    |
| admin_id    | BIGINT       | FK → users                                   | Admin thực hiện    |
| action      | VARCHAR(20)  | NOT NULL, CHECK (APPROVED/REJECTED/SUSPENDED/REACTIVATED) | Hành động |
| reason      | VARCHAR(500) |                                              | Lý do              |
| created_at  | DATETIME     | DEFAULT GETDATE()                            | Thời điểm          |

```sql
CREATE TABLE [dbo].[facility_approval_logs](
    [id] [bigint] IDENTITY(1,1) NOT NULL,
    [facility_id] [bigint] NOT NULL,
    [admin_id] [bigint] NOT NULL,
    [action] [varchar](20) NOT NULL,
    [reason] [varchar](500) NULL,
    [created_at] [datetime] NOT NULL,
    CONSTRAINT [PK_facility_approval_logs] PRIMARY KEY CLUSTERED ([id] ASC),
    FOREIGN KEY ([facility_id]) REFERENCES [facilities]([id]),
    FOREIGN KEY ([admin_id]) REFERENCES [users]([id])
);
```

---

### 2.6 courts

Thông tin sân thi đấu thuộc cơ sở.

| Cột          | Kiểu          | Ràng buộc               | Mô tả                    |
| ------------ | ------------- | ----------------------- | ------------------------ |
| id           | BIGINT        | PK, IDENTITY            | Mã sân                   |
| facility_id  | BIGINT        | FK → facilities         | Cơ sở chứa sân           |
| sport_type_id| INT           | FK → sport_types        | Loại thể thao            |
| name         | NVARCHAR(100) | NOT NULL, UNIQUE(facility_id, name) | Tên sân     |
| description  | VARCHAR(500)  |                         | Mô tả sân                |
| surface_type | NVARCHAR(50)  |                         | Loại mặt sân             |
| is_indoor    | BIT           | DEFAULT 0               | Sân trong nhà            |
| is_active    | BIT           | DEFAULT 1               | Đang hoạt động           |
| created_at   | DATETIME      | DEFAULT GETDATE()       | Ngày tạo                 |

```sql
CREATE TABLE [dbo].[courts](
    [id] [bigint] IDENTITY(1,1) NOT NULL,
    [facility_id] [bigint] NOT NULL,
    [sport_type_id] [int] NOT NULL,
    [name] [nvarchar](100) NOT NULL,
    [description] [varchar](500) NULL,
    [surface_type] [nvarchar](50) NULL,
    [is_indoor] [bit] NOT NULL,
    [is_active] [bit] NOT NULL,
    [created_at] [datetime] NOT NULL,
    CONSTRAINT [PK_courts] PRIMARY KEY CLUSTERED ([id] ASC),
    FOREIGN KEY ([facility_id]) REFERENCES [facilities]([id]),
    FOREIGN KEY ([sport_type_id]) REFERENCES [sport_types]([id])
);
```

---

### 2.7 time_slots

Khung giờ đặt sân theo từng sân cụ thể.

| Cột          | Kiểu           | Ràng buộc                    | Mô tả                       |
| ------------ | -------------- | ---------------------------- | --------------------------- |
| id           | BIGINT         | PK, IDENTITY                 | Mã khung giờ                |
| court_id     | BIGINT         | FK → courts                  | Sân áp dụng                 |
| day_of_week  | TINYINT        | CHECK (NULL hoặc 0–6)        | Thứ trong tuần (NULL = mọi ngày) |
| start_time   | TIME           | NOT NULL                     | Giờ bắt đầu                 |
| end_time     | TIME           | NOT NULL, CHECK (> start_time) | Giờ kết thúc              |
| price        | DECIMAL(12,0)  | NOT NULL, CHECK (> 0)        | Giá thuê sân                |
| deposit_rate | DECIMAL(5,2)   | DEFAULT 30.00, CHECK (0–100) | Tỷ lệ đặt cọc (%)           |
| is_active    | BIT            | DEFAULT 1                    | Đang hoạt động              |

```sql
CREATE TABLE [dbo].[time_slots](
    [id] [bigint] IDENTITY(1,1) NOT NULL,
    [court_id] [bigint] NOT NULL,
    [day_of_week] [tinyint] NULL,
    [start_time] [time](7) NOT NULL,
    [end_time] [time](7) NOT NULL,
    [price] [decimal](12, 0) NOT NULL,
    [deposit_rate] [decimal](5, 2) NOT NULL,
    [is_active] [bit] NOT NULL,
    CONSTRAINT [PK_time_slots] PRIMARY KEY CLUSTERED ([id] ASC),
    FOREIGN KEY ([court_id]) REFERENCES [courts]([id])
);
```

---

### 2.8 bookings

Thông tin đặt sân của khách hàng.

| Cột               | Kiểu           | Ràng buộc                                                              | Mô tả                        |
| ----------------- | -------------- | ---------------------------------------------------------------------- | ---------------------------- |
| id                | BIGINT         | PK, IDENTITY                                                           | Mã đặt sân                   |
| booking_code      | VARCHAR(255)   | UNIQUE                                                                 | Mã đơn (dạng SB-xxxx)        |
| owner_id          | BIGINT         | FK → users (CASCADE)                                                   | Chủ cơ sở                    |
| customer_id       | BIGINT         | FK → users                                                             | Khách hàng đặt sân           |
| court_id          | BIGINT         | FK → courts                                                            | Sân được đặt                 |
| time_slot_id      | BIGINT         | FK → time_slots                                                        | Khung giờ                    |
| booking_date      | DATE           | NOT NULL                                                               | Ngày đặt sân                 |
| start_time        | TIME           | NOT NULL                                                               | Giờ bắt đầu                  |
| end_time          | TIME           | NOT NULL                                                               | Giờ kết thúc                 |
| total_price       | DECIMAL(12,0)  | NOT NULL, CHECK (> 0)                                                  | Tổng tiền                    |
| deposit_amount    | DECIMAL(12,0)  | NOT NULL, CHECK (≥ 0)                                                  | Tiền cọc                     |
| commission_amount | DECIMAL(12,0)  | DEFAULT 0, CHECK (≥ 0)                                                 | Tiền hoa hồng                |
| status            | VARCHAR(20)    | DEFAULT 'PENDING_PAYMENT', CHECK (PENDING_PAYMENT/PENDING_CONFIRM/CONFIRMED/CHECKED_IN/COMPLETED/CANCELLED/REJECTED) | Trạng thái đơn |
| note              | NVARCHAR(500)  |                                                                        | Ghi chú                      |
| cancel_reason     | NVARCHAR(500)  |                                                                        | Lý do hủy                    |
| cancelled_at      | DATETIME       |                                                                        | Thời điểm hủy                |
| created_at        | DATETIME       | DEFAULT GETDATE()                                                      | Ngày tạo                     |
| updated_at        | DATETIME       |                                                                        | Ngày cập nhật                |

```sql
CREATE TABLE [dbo].[bookings](
    [id] [bigint] IDENTITY(1,1) NOT NULL,
    [booking_code] [varchar](255) NULL,
    [owner_id] [bigint] NOT NULL,
    [customer_id] [bigint] NOT NULL,
    [court_id] [bigint] NOT NULL,
    [time_slot_id] [bigint] NOT NULL,
    [booking_date] [date] NOT NULL,
    [start_time] [time](7) NOT NULL,
    [end_time] [time](7) NOT NULL,
    [total_price] [decimal](12, 0) NOT NULL,
    [deposit_amount] [decimal](12, 0) NOT NULL,
    [commission_amount] [decimal](12, 0) NOT NULL,
    [status] [varchar](20) NOT NULL,
    [note] [nvarchar](500) NULL,
    [cancelled_at] [datetime] NULL,
    [cancel_reason] [nvarchar](500) NULL,
    [created_at] [datetime] NOT NULL,
    [updated_at] [datetime] NULL,
    CONSTRAINT [PK_bookings] PRIMARY KEY CLUSTERED ([id] ASC),
    CONSTRAINT [UQ_bookings_code] UNIQUE ([booking_code]),
    FOREIGN KEY ([owner_id]) REFERENCES [users]([id]) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY ([customer_id]) REFERENCES [users]([id]),
    FOREIGN KEY ([court_id]) REFERENCES [courts]([id]),
    FOREIGN KEY ([time_slot_id]) REFERENCES [time_slots]([id])
);
```

---

### 2.9 payments

Thông tin thanh toán qua VNPay.

| Cột             | Kiểu           | Ràng buộc                            | Mô tả                     |
| --------------- | -------------- | ------------------------------------ | ------------------------- |
| id              | BIGINT         | PK, IDENTITY                         | Mã thanh toán             |
| booking_id      | BIGINT         | FK → bookings                        | Đơn đặt sân liên quan     |
| vnpay_txn_ref   | VARCHAR(50)    | NOT NULL, UNIQUE                     | Mã tham chiếu VNPay       |
| vnpay_txn_no    | VARCHAR(50)    |                                      | Mã giao dịch VNPay        |
| amount          | DECIMAL(12,0)  | NOT NULL, CHECK (> 0)                | Số tiền                   |
| payment_type    | VARCHAR(20)    | NOT NULL, CHECK (DEPOSIT/REFUND)     | Loại thanh toán           |
| status          | VARCHAR(20)    | DEFAULT 'PENDING', CHECK (PENDING/SUCCESS/FAILED/REFUNDED) | Trạng thái |
| payment_method  | VARCHAR(50)    |                                      | Phương thức               |
| bank_code       | VARCHAR(20)    |                                      | Mã ngân hàng              |
| paid_at         | DATETIME       |                                      | Thời điểm thanh toán      |
| description     | NVARCHAR(MAX)  |                                      | Mô tả giao dịch           |
| transaction_ref | VARCHAR(255)   |                                      | Mã tham chiếu nội bộ      |
| created_at      | DATETIME       | DEFAULT GETDATE()                    | Ngày tạo                  |

```sql
CREATE TABLE [dbo].[payments](
    [id] [bigint] IDENTITY(1,1) NOT NULL,
    [booking_id] [bigint] NOT NULL,
    [vnpay_txn_ref] [varchar](50) NOT NULL,
    [vnpay_txn_no] [varchar](50) NULL,
    [amount] [decimal](12, 0) NOT NULL,
    [payment_type] [varchar](20) NOT NULL,
    [status] [varchar](20) NULL,
    [payment_method] [varchar](50) NULL,
    [bank_code] [varchar](20) NULL,
    [paid_at] [datetime] NULL,
    [created_at] [datetime] NOT NULL,
    [description] [nvarchar](max) NULL,
    [transaction_ref] [varchar](255) NULL,
    CONSTRAINT [PK_payments] PRIMARY KEY CLUSTERED ([id] ASC),
    CONSTRAINT [UQ_payments_txn_ref] UNIQUE ([vnpay_txn_ref]),
    FOREIGN KEY ([booking_id]) REFERENCES [bookings]([id])
);
```

---

### 2.10 check_ins

Xác nhận check-in khi khách hàng đến sân.

| Cột          | Kiểu          | Ràng buộc         | Mô tả                    |
| ------------ | ------------- | ----------------- | ------------------------ |
| id           | BIGINT        | PK, IDENTITY      | Mã check-in              |
| booking_id   | BIGINT        | FK → bookings, UNIQUE | Đơn đặt sân (1 check-in / booking) |
| checked_by   | BIGINT        | FK → users        | Người thực hiện check-in |
| checked_in_at| DATETIME      | DEFAULT GETDATE() | Thời điểm check-in       |
| note         | NVARCHAR(300) |                   | Ghi chú                  |

```sql
CREATE TABLE [dbo].[check_ins](
    [id] [bigint] IDENTITY(1,1) NOT NULL,
    [booking_id] [bigint] NOT NULL,
    [checked_by] [bigint] NOT NULL,
    [checked_in_at] [datetime] NOT NULL,
    [note] [nvarchar](300) NULL,
    CONSTRAINT [PK_check_ins] PRIMARY KEY CLUSTERED ([id] ASC),
    CONSTRAINT [UQ_check_ins_booking] UNIQUE ([booking_id]),
    FOREIGN KEY ([booking_id]) REFERENCES [bookings]([id]),
    FOREIGN KEY ([checked_by]) REFERENCES [users]([id])
);
```

---

### 2.11 reviews

Đánh giá cơ sở sau khi hoàn thành booking.

| Cột        | Kiểu           | Ràng buộc             | Mô tả                       |
| ---------- | -------------- | --------------------- | --------------------------- |
| id         | BIGINT         | PK, IDENTITY          | Mã đánh giá                 |
| booking_id | BIGINT         | FK → bookings, UNIQUE | Đơn đặt sân (1 review / booking) |
| customer_id| BIGINT         | FK → users            | Khách hàng đánh giá         |
| facility_id| BIGINT         | FK → facilities       | Cơ sở được đánh giá         |
| rating     | TINYINT        | NOT NULL, CHECK (1–5) | Điểm đánh giá               |
| comment    | VARCHAR(1000)  |                       | Nội dung nhận xét           |
| owner_reply| NVARCHAR(500)  |                       | Phản hồi của chủ sân        |
| is_visible | BIT            | DEFAULT 1             | Hiển thị công khai          |
| created_at | DATETIME       | DEFAULT GETDATE()     | Ngày đánh giá               |
| updated_at | DATETIME       |                       | Ngày cập nhật               |

```sql
CREATE TABLE [dbo].[reviews](
    [id] [bigint] IDENTITY(1,1) NOT NULL,
    [booking_id] [bigint] NOT NULL,
    [customer_id] [bigint] NOT NULL,
    [facility_id] [bigint] NOT NULL,
    [rating] [tinyint] NOT NULL,
    [comment] [varchar](1000) NULL,
    [owner_reply] [nvarchar](500) NULL,
    [is_visible] [bit] NOT NULL,
    [created_at] [datetime] NOT NULL,
    [updated_at] [datetime] NULL,
    CONSTRAINT [PK_reviews] PRIMARY KEY CLUSTERED ([id] ASC),
    CONSTRAINT [UQ_reviews_booking] UNIQUE ([booking_id]),
    FOREIGN KEY ([booking_id]) REFERENCES [bookings]([id]),
    FOREIGN KEY ([customer_id]) REFERENCES [users]([id]),
    FOREIGN KEY ([facility_id]) REFERENCES [facilities]([id])
);
```

---

### 2.12 notifications

Thông báo hệ thống gửi đến người dùng.

| Cột          | Kiểu         | Ràng buộc         | Mô tả                          |
| ------------ | ------------ | ----------------- | ------------------------------ |
| id           | BIGINT       | PK, IDENTITY      | Mã thông báo                   |
| user_id      | BIGINT       | FK → users        | Người nhận                     |
| type         | VARCHAR(50)  |                   | Loại thông báo (BOOKING_CREATED, FACILITY_APPROVED,…) |
| title        | VARCHAR(200) |                   | Tiêu đề                        |
| message      | VARCHAR(500) |                   | Nội dung                       |
| ref_id       | BIGINT       |                   | ID tham chiếu (booking/facility) |
| ref_type     | VARCHAR(50)  |                   | Loại tham chiếu (BOOKING/FACILITY) |
| is_read      | BIT          | DEFAULT 0         | Đã đọc chưa                    |
| created_at   | DATETIME     | DEFAULT GETDATE() | Ngày tạo                       |

```sql
CREATE TABLE [dbo].[notifications](
    [id] [bigint] IDENTITY(1,1) NOT NULL,
    [user_id] [bigint] NOT NULL,
    [type] [varchar](50) NULL,
    [title] [varchar](200) NULL,
    [message] [varchar](500) NULL,
    [ref_id] [bigint] NULL,
    [ref_type] [varchar](50) NULL,
    [is_read] [bit] NOT NULL,
    [created_at] [datetime] NOT NULL,
    CONSTRAINT [PK_notifications] PRIMARY KEY CLUSTERED ([id] ASC),
    FOREIGN KEY ([user_id]) REFERENCES [users]([id])
);
```

---

## 3. QUY TẮC NGHIỆP VỤ

* **Phân quyền 3 tầng:** ADMIN quản lý hệ thống, OWNER quản lý cơ sở, CUSTOMER đặt sân
* **Duyệt cơ sở:** Cơ sở mới tạo có status `PENDING`, cần Admin duyệt (`APPROVED`) mới hoạt động được
* **Luồng đặt sân:**
  1. Customer tạo booking → status `PENDING_PAYMENT`
  2. Thanh toán cọc VNPay thành công → status `PENDING_CONFIRM`
  3. Owner xác nhận → status `CONFIRMED`
  4. Khách đến check-in → status `CHECKED_IN`
  5. Hoàn thành → status `COMPLETED`
* **Ràng buộc đặt sân:** Không cho phép đặt trùng (court + time_slot + booking_date)
* **Review:** Chỉ 1 đánh giá duy nhất cho mỗi booking (UNIQUE booking_id)
* **Check-in:** Chỉ 1 lần check-in cho mỗi booking (UNIQUE booking_id)
* **Hoa hồng:** Mỗi booking ghi nhận `commission_amount` = `total_price × commission_rate` của cơ sở

---

## 4. DỮ LIỆU TEST

| Role     | Email            | Mật khẩu |
| -------- | ---------------- | --------- |
| Admin    | duc@gmail.com    | (hashed)  |
| Owner    | duc1@gmail.com   | (hashed)  |
| Customer | duc2@gmail.com   | (hashed)  |
| Customer | duc3@gmail.com   | (hashed)  |

---

## 5. KẾT LUẬN

Database đảm bảo:

* Tính toàn vẹn dữ liệu qua ràng buộc FK, CHECK, UNIQUE
* Hỗ trợ đầy đủ luồng nghiệp vụ: đăng ký cơ sở → duyệt → đặt sân → thanh toán → check-in → đánh giá
* Tích hợp thanh toán VNPay với lịch sử giao dịch chi tiết
* Hệ thống thông báo real-time theo sự kiện
* Dễ mở rộng thêm loại hình thể thao, cơ sở, tính năng mới

---

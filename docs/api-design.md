# SPORTS FACILITY BOOKING SYSTEM

**API Design Documentation**

| | |
|---|---|
| **Tài liệu** | API Design Documentation v2.0 |
| **Tổng số API** | 74 APIs chia thành 13 nhóm chức năng |
| **Chuẩn** | RESTful API — JSON Request/Response |
| **Xác thực** | JWT Bearer Token (`Authorization: Bearer <token>`) |
| **Base URL** | `http://localhost:8080` |
| **Content-Type** | `application/json` |
| **Công nghệ test** | Postman Collection + JUnit 5 + Mockito |

---

# 1. Quy Ước Chung

## 1.1 Cấu Trúc Response Chuẩn

*Mọi API đều trả về JSON theo cấu trúc thống nhất: `{ success, data, message, timestamp }`*

| **Loại Response** | **Cấu Trúc JSON** |
|---|---|
|  **Success Response** | `{ "success": true, "data": { ... }, "message": "Thành công", "timestamp": "2025-02-10T10:30:00Z" }` |
|  **Error Response** | `{ "success": false, "data": null, "error": { "code": "BOOKING_CONFLICT", "message": "Khung giờ đã được đặt" }, "timestamp": "2025-02-10T10:30:00Z" }` |
|  **Paginated Response** | `{ "success": true, "data": { "content": [...], "totalPages": 5, "totalElements": 48, "currentPage": 0, "pageSize": 10 } }` |

## 1.2 HTTP Status Codes

| **Status Code** | **Ý Nghĩa** | **Ví Dụ** |
|---|---|---|
| **200 OK** | Thành công, trả về dữ liệu | GET, PUT, PATCH thành công |
| **201 Created** | Tạo mới thành công | POST thành công |
| **204 No Content** | Thành công, không có data | DELETE thành công |
| **400 Bad Request** | Dữ liệu gửi lên không hợp lệ | Thiếu field, sai định dạng |
| **401 Unauthorized** | Chưa xác thực hoặc token hết hạn | Thiếu/sai JWT token |
| **403 Forbidden** | Không có quyền thực hiện | Sai role, không phải chủ sở hữu |
| **404 Not Found** | Resource không tồn tại | ID không hợp lệ |
| **409 Conflict** | Xung đột dữ liệu | Email trùng, booking trùng giờ |
| **500 Internal Server Error** | Lỗi server không mong muốn | Exception chưa xử lý |

## 1.3 Authentication

*Header bắt buộc với API cần xác thực: `Authorization: Bearer <JWT_TOKEN>`*

| **Auth Level** | **Điều Kiện** | **Ví Dụ API** |
|---|---|---|
| **Public** | Không cần token. Mở cho tất cả người dùng. | Tìm kiếm sân, xem chi tiết |
| **CUSTOMER** | JWT token với role = CUSTOMER | Đặt sân, xem lịch sử booking |
| **OWNER** | JWT token với role = OWNER | Quản lý sân, xác nhận booking |
| **ADMIN** | JWT token với role = ADMIN | Duyệt cơ sở, quản lý người dùng |
| **Authenticated** | Bất kỳ role nào đã đăng nhập | Xem profile, thông báo |

---

# 2. Tổng Hợp Danh Sách API

*Tổng cộng 74 APIs chia thành 13 nhóm. Ký hiệu \* trong cột Request là trường bắt buộc.*

| **#** | **API ID** | **Method** | **Endpoint** | **Tên API** | **Auth** |
|---|---|---|---|---|---|
| **Authentication \[UC-01, UC-02, UC-03\]** | | | | | |
| 1 | **API-01** | **POST** | `/api/auth/register` | Đăng Ký Tài Khoản | **Public** |
| 2 | **API-02** | **POST** | `/api/auth/login` | Đăng Nhập | **Public** |
| 3 | **API-03** | **GET** | `/api/auth/profile` | Lấy Thông Tin Profile | **Authenticated** |
| 4 | **API-04** | **PUT** | `/api/auth/profile` | Cập Nhật Profile | **Authenticated** |
| 5 | **API-05** | **POST** | `/api/auth/change-password` | Đổi Mật Khẩu | **Authenticated** |
| 6 | **API-06** | **POST** | `/api/auth/update-password` | Cập Nhật Mật Khẩu | **Authenticated** |
| **Users (Người Dùng)** | | | | | |
| 7 | **API-07** | **POST** | `/api/users` | Tạo Người Dùng | **ADMIN** |
| 8 | **API-08** | **GET** | `/api/users` | Danh Sách Người Dùng | **ADMIN** |
| 9 | **API-09** | **GET** | `/api/users/{id}` | Chi Tiết Người Dùng | **Authenticated** |
| 10 | **API-10** | **PUT** | `/api/users/{id}` | Cập Nhật Người Dùng | **Authenticated** |
| 11 | **API-11** | **DELETE** | `/api/users/{id}` | Vô Hiệu Hóa Tài Khoản | **ADMIN** |
| 12 | **API-12** | **PUT** | `/api/users/{id}/lock` | Khóa Tài Khoản | **ADMIN** |
| 13 | **API-13** | **PUT** | `/api/users/{id}/unlock` | Mở Khóa Tài Khoản | **ADMIN** |
| **Sport Types (Loại Sân) \[UC-14\]** | | | | | |
| 14 | **API-14** | **GET** | `/api/sport-types` | Danh Sách Loại Sân (Active) | **Public** |
| 15 | **API-15** | **GET** | `/api/sport-types/all` | Tất Cả Loại Sân | **Public** |
| 16 | **API-16** | **GET** | `/api/sport-types/{id}` | Chi Tiết Loại Sân | **Public** |
| 17 | **API-17** | **POST** | `/api/sport-types` | Tạo Loại Sân Mới | **Public** |
| 18 | **API-18** | **PUT** | `/api/sport-types/{id}` | Cập Nhật Loại Sân | **ADMIN** |
| **Facilities (Cơ Sở Thể Thao) \[UC-03, UC-04, UC-08, UC-12\]** | | | | | |
| 19 | **API-19** | **GET** | `/api/facilities` | Tìm Kiếm & Danh Sách Cơ Sở | **Public** |
| 20 | **API-20** | **GET** | `/api/facilities/{id}` | Chi Tiết Cơ Sở | **Public** |
| 21 | **API-21** | **GET** | `/api/facilities/owner/{ownerId}` | Cơ Sở Theo Chủ Sân | **Authenticated** |
| 22 | **API-22** | **GET** | `/api/facilities/admin/all` | Tất Cả Cơ Sở (Admin) | **ADMIN** |
| 23 | **API-23** | **POST** | `/api/facilities` | Tạo Cơ Sở Mới | **OWNER** |
| 24 | **API-24** | **PUT** | `/api/facilities/{id}` | Cập Nhật Cơ Sở | **OWNER** |
| 25 | **API-25** | **PATCH** | `/api/facilities/{id}/cover-image` | Cập Nhật Ảnh Bìa Cơ Sở | **OWNER** |
| 26 | **API-26** | **DELETE** | `/api/facilities/{id}` | Xóa Cơ Sở | **OWNER** |
| 27 | **API-27** | **POST** | `/api/facilities/{id}/approve` | Duyệt Cơ Sở | **ADMIN** |
| 28 | **API-28** | **POST** | `/api/facilities/{id}/reject` | Từ Chối Cơ Sở | **ADMIN** |
| **Facility Images (Ảnh Cơ Sở)** | | | | | |
| 29 | **API-29** | **GET** | `/api/facility-images/facility/{facilityId}` | Danh Sách Ảnh Cơ Sở | **Public** |
| 30 | **API-30** | **POST** | `/api/facility-images/facility/{facilityId}` | Thêm Ảnh Cơ Sở | **OWNER** |
| 31 | **API-31** | **POST** | `/api/facility-images/facility/{facilityId}/bulk` | Thêm Nhiều Ảnh Cơ Sở | **OWNER** |
| 32 | **API-32** | **DELETE** | `/api/facility-images/{imageId}` | Xóa Ảnh | **OWNER** |
| 33 | **API-33** | **DELETE** | `/api/facility-images/facility/{facilityId}/all` | Xóa Tất Cả Ảnh Cơ Sở | **OWNER** |
| 34 | **API-34** | **PUT** | `/api/facility-images/{imageId}/sort-order` | Cập Nhật Thứ Tự Ảnh | **OWNER** |
| **Courts (Sân) \[UC-08, UC-09\]** | | | | | |
| 35 | **API-35** | **GET** | `/api/courts/facility/{facilityId}` | Danh Sách Sân Theo Cơ Sở | **Public** |
| 36 | **API-36** | **GET** | `/api/courts/{id}` | Chi Tiết Sân | **Public** |
| 37 | **API-37** | **GET** | `/api/courts/search` | Tìm Kiếm Sân | **Public** |
| 38 | **API-38** | **POST** | `/api/courts` | Tạo Sân Mới | **OWNER** |
| 39 | **API-39** | **PUT** | `/api/courts/{id}` | Cập Nhật Thông Tin Sân | **OWNER** |
| 40 | **API-40** | **DELETE** | `/api/courts/{id}` | Xóa Sân | **OWNER** |
| **Time Slots (Khung Giờ & Giá) \[UC-09\]** | | | | | |
| 41 | **API-41** | **GET** | `/api/timeslots` | Tất Cả Khung Giờ | **Public** |
| 42 | **API-42** | **GET** | `/api/timeslots/court/{courtId}` | Khung Giờ Theo Sân | **Public** |
| 43 | **API-43** | **POST** | `/api/timeslots` | Tạo Khung Giờ | **OWNER** |
| 44 | **API-44** | **PUT** | `/api/timeslots/{id}` | Cập Nhật Khung Giờ | **OWNER** |
| 45 | **API-45** | **DELETE** | `/api/timeslots/{id}` | Xóa Khung Giờ | **OWNER** |
| **Bookings (Đặt Sân) \[UC-05, UC-06, UC-10, UC-11\]** | | | | | |
| 46 | **API-46** | **GET** | `/api/bookings` | Thông Tin Endpoints Booking | **Authenticated** |
| 47 | **API-47** | **POST** | `/api/bookings` | Tạo Booking Mới | **CUSTOMER** |
| 48 | **API-48** | **GET** | `/api/bookings/my-bookings` | Lịch Sử Đặt Sân Của Tôi | **CUSTOMER** |
| 49 | **API-49** | **GET** | `/api/bookings/owner/pending-bookings` | Danh Sách Booking Chờ Duyệt | **OWNER** |
| 50 | **API-50** | **GET** | `/api/bookings/owner/all-bookings` | Tất Cả Booking Của Owner | **OWNER** |
| 51 | **API-51** | **GET** | `/api/bookings/code/{bookingCode}` | Chi Tiết Booking Theo Mã | **Authenticated** |
| 52 | **API-52** | **GET** | `/api/bookings/{id}` | Chi Tiết Booking Theo ID | **Authenticated** |
| 53 | **API-53** | **GET** | `/api/bookings/court/{courtId}` | Lịch Đặt Sân Theo Court | **Public** |
| 54 | **API-54** | **POST** | `/api/bookings/{id}/confirm` | Xác Nhận Booking | **OWNER** |
| 55 | **API-55** | **POST** | `/api/bookings/{id}/cancel` | Hủy Booking | **OWNER** |
| 56 | **API-56** | **DELETE** | `/api/bookings/{id}` | Xóa Booking | **Authenticated** |
| **Payments (Thanh Toán)** | | | | | |
| 57 | **API-57** | **POST** | `/api/payments/{bookingId}/vnpay` | Tạo URL Thanh Toán VNPay | **Authenticated** |
| 58 | **API-58** | **GET** | `/api/payments/vnpay/return` | Xử Lý Kết Quả VNPay Return | **Public** |
| 59 | **API-59** | **POST** | `/api/payments/vnpay/ipn` | Xử Lý VNPay IPN | **Public** |
| 60 | **API-60** | **GET** | `/api/payments/booking/{bookingId}` | Danh Sách Thanh Toán Theo Booking | **Authenticated** |
| 61 | **API-61** | **POST** | `/api/payments/{paymentId}/refund` | Hoàn Tiền | **ADMIN** |
| **Reviews (Đánh Giá) \[UC-07\]** | | | | | |
| 62 | **API-62** | **GET** | `/api/reviews` | Tất Cả Đánh Giá | **Public** |
| 63 | **API-63** | **POST** | `/api/reviews` | Tạo Đánh Giá | **CUSTOMER** |
| 64 | **API-64** | **GET** | `/api/reviews/facility/{facilityId}` | Đánh Giá Theo Cơ Sở | **Public** |
| 65 | **API-65** | **PUT** | `/api/reviews/{id}/reply` | Phản Hồi Đánh Giá | **OWNER** |
| **Notifications (Thông Báo)** | | | | | |
| 66 | **API-66** | **GET** | `/api/notifications` | Danh Sách Thông Báo | **Authenticated** |
| 67 | **API-67** | **GET** | `/api/notifications/latest` | Thông Báo Mới Nhất | **Authenticated** |
| 68 | **API-68** | **GET** | `/api/notifications/unread-count` | Số Thông Báo Chưa Đọc | **Authenticated** |
| 69 | **API-69** | **PUT** | `/api/notifications/{id}/read` | Đánh Dấu Đã Đọc | **Authenticated** |
| 70 | **API-70** | **PUT** | `/api/notifications/read-all` | Đánh Dấu Tất Cả Đã Đọc | **Authenticated** |
| **Check-ins** | | | | | |
| 71 | **API-71** | **POST** | `/api/checkins` | Check-In Khách | **OWNER** |
| 72 | **API-72** | **GET** | `/api/checkins/booking/{bookingId}` | Thông Tin Check-In Theo Booking | **Authenticated** |
| **Upload** | | | | | |
| 73 | **API-73** | **POST** | `/api/upload` | Upload File / Ảnh | **Authenticated** |
| **Admin** | | | | | |
| 74 | **API-74** | **GET** | `/api/admin/health` | Kiểm Tra Sức Khỏe Hệ Thống | **Public** |

---

# 3. Chi Tiết Từng Nhóm API

## 3.1 Authentication

| **API** | **API-01** |
|---|---|
| **Method** | POST |
| **Endpoint** | `/api/auth/register` |
| **Auth** | Public |
| **Request Body** | `{ "fullName*": "string", "email*": "string", "password*": "string", "phone": "string" }` |
| **Response** | `{ "success": true, "data": { "token": "JWT", "user": { ... } } }` |

| **API** | **API-02** |
|---|---|
| **Method** | POST |
| **Endpoint** | `/api/auth/login` |
| **Auth** | Public |
| **Request Body** | `{ "email*": "string", "password*": "string" }` |
| **Response** | `{ "success": true, "data": { "token": "JWT", "user": { ... } } }` |

| **API** | **API-03** |
|---|---|
| **Method** | GET |
| **Endpoint** | `/api/auth/profile` |
| **Auth** | Authenticated (userId từ JWT) |
| **Response** | `{ "success": true, "data": { "id": 1, "email": "...", "fullName": "...", "role": "..." } }` |

| **API** | **API-04** |
|---|---|
| **Method** | PUT |
| **Endpoint** | `/api/auth/profile` |
| **Auth** | Authenticated (userId từ JWT) |
| **Request Body** | `{ "fullName": "string", "phone": "string", "avatarUrl": "string" }` |
| **Response** | `{ "success": true, "data": { ... } }` |

| **API** | **API-05** |
|---|---|
| **Method** | POST |
| **Endpoint** | `/api/auth/change-password` |
| **Auth** | Authenticated |
| **Query Params** | `userId*`, `oldPassword*`, `newPassword*` |
| **Response** | `{ "success": true, "data": { ... } }` |

| **API** | **API-06** |
|---|---|
| **Method** | POST |
| **Endpoint** | `/api/auth/update-password` |
| **Auth** | Authenticated (userId từ JWT) |
| **Request Body** | `{ "currentPassword*": "string", "newPassword*": "string" }` |
| **Response** | `{ "success": true, "data": { ... } }` |

---

## 3.2 Users

| **API** | **API-07** |
|---|---|
| **Method** | POST |
| **Endpoint** | `/api/users` |
| **Auth** | ADMIN |
| **Request Body** | `{ "fullName*": "string", "email*": "string", "password*": "string", "phone": "string" }` |
| **Response** | `{ "success": true, "data": { ... } }` |

| **API** | **API-08** |
|---|---|
| **Method** | GET |
| **Endpoint** | `/api/users` |
| **Auth** | ADMIN |
| **Query Params** | `page` (default: 0), `size` (default: 10) |
| **Response** | `{ "success": true, "data": { "content": [...], "totalPages": N } }` |

| **API** | **API-09 → API-13** |
|---|---|
| **Endpoints** | GET/PUT `/api/users/{id}`, DELETE `/api/users/{id}`, PUT `/api/users/{id}/lock`, PUT `/api/users/{id}/unlock` |
| **Auth** | ADMIN |

---

## 3.3 Sport Types

| **API** | **API-14** |
|---|---|
| **Method** | GET |
| **Endpoint** | `/api/sport-types` |
| **Auth** | Public |
| **Response** | `{ "success": true, "data": [ { "id": 1, "name": "Bóng đá", "isActive": true } ] }` |

| **API** | **API-17** |
|---|---|
| **Method** | POST |
| **Endpoint** | `/api/sport-types` |
| **Auth** | Public |
| **Request Body** | `{ "name*": "string", "description": "string", "isActive": true }` |
| **Response** | `{ "success": true, "data": { ... } }` |

---

## 3.4 Facilities

| **API** | **API-19** |
|---|---|
| **Method** | GET |
| **Endpoint** | `/api/facilities` |
| **Auth** | Public |
| **Query Params** | `page` (default: 0), `size` (default: 10), `search` (default: "") |
| **Response** | `{ "success": true, "data": { "content": [...], "totalPages": N } }` |

| **API** | **API-23** |
|---|---|
| **Method** | POST |
| **Endpoint** | `/api/facilities` |
| **Auth** | OWNER |
| **Query Params** | `ownerId*` |
| **Request Body** | `{ "name*": "string", "address*": "string", "sportTypeId*": 1, ... }` |
| **Response** | `{ "success": true, "data": { ... } }` |

| **API** | **API-27** |
|---|---|
| **Method** | POST |
| **Endpoint** | `/api/facilities/{id}/approve` |
| **Auth** | ADMIN |
| **Query Params** | `adminId*` |
| **Response** | `{ "success": true, "data": null, "message": "Phê duyệt sân thành công" }` |

| **API** | **API-28** |
|---|---|
| **Method** | POST |
| **Endpoint** | `/api/facilities/{id}/reject` |
| **Auth** | ADMIN |
| **Query Params** | `adminId*`, `reason*` |
| **Response** | `{ "success": true, "data": null, "message": "Từ chối sân thành công" }` |

---

## 3.5 Courts

| **API** | **API-35** |
|---|---|
| **Method** | GET |
| **Endpoint** | `/api/courts/facility/{facilityId}` |
| **Auth** | Public |
| **Response** | `{ "success": true, "data": [ { "id": 1, "name": "Sân A", ... } ] }` |

| **API** | **API-37** |
|---|---|
| **Method** | GET |
| **Endpoint** | `/api/courts/search` |
| **Auth** | Public |
| **Query Params** | `address` (optional), `sportTypeId` (optional) |
| **Response** | `{ "success": true, "data": [ ... ] }` |

| **API** | **API-38** |
|---|---|
| **Method** | POST |
| **Endpoint** | `/api/courts` |
| **Auth** | OWNER |
| **Request Body** | `{ "facilityId*": 1, "name*": "string", "description": "string", ... }` |
| **Response** | `{ "success": true, "data": { ... } }` |

---

## 3.6 Time Slots

| **API** | **API-42** |
|---|---|
| **Method** | GET |
| **Endpoint** | `/api/timeslots/court/{courtId}` |
| **Auth** | Public |
| **Response** | `{ "success": true, "data": [ { "id": 1, "startTime": "07:00", "endTime": "08:00", "price": 200000 } ] }` |

| **API** | **API-43** |
|---|---|
| **Method** | POST |
| **Endpoint** | `/api/timeslots` |
| **Auth** | OWNER |
| **Request Body** | `{ "courtId*": 1, "startTime*": "07:00", "endTime*": "08:00", "price*": 200000, "depositRate": 30, "dayOfWeek": null }` |
| **Response** | `{ "success": true, "data": { ... } }` |

---

## 3.7 Bookings

| **API** | **API-47** |
|---|---|
| **Method** | POST |
| **Endpoint** | `/api/bookings` |
| **Auth** | CUSTOMER (userId từ JWT) |
| **Request Body** | `{ "courtId*": 1, "timeSlotId*": 1, "bookingDate*": "yyyy-MM-dd" }` |
| **Response** | `{ "success": true, "data": { "id": 1, "bookingCode": "SB-...", "status": "PENDING_PAYMENT" } }` |

| **API** | **API-48** |
|---|---|
| **Method** | GET |
| **Endpoint** | `/api/bookings/my-bookings` |
| **Auth** | CUSTOMER (userId từ JWT) |
| **Query Params** | `page` (default: 0), `size` (default: 10) |
| **Response** | `{ "success": true, "data": { "content": [...], "totalPages": N } }` |

| **API** | **API-54** |
|---|---|
| **Method** | POST |
| **Endpoint** | `/api/bookings/{id}/confirm` |
| **Auth** | OWNER |
| **Query Params** | `ownerId*` |
| **Response** | `{ "success": true, "data": { "status": "CONFIRMED" } }` |

| **API** | **API-55** |
|---|---|
| **Method** | POST |
| **Endpoint** | `/api/bookings/{id}/cancel` |
| **Auth** | OWNER |
| **Query Params** | `reason` (optional) |
| **Response** | `{ "success": true, "data": { "status": "CANCELLED" } }` |

---

## 3.8 Payments

| **API** | **API-57** |
|---|---|
| **Method** | POST |
| **Endpoint** | `/api/payments/{bookingId}/vnpay` |
| **Auth** | Authenticated |
| **Query Params** | `returnUrl*`, `bankCode` (optional), `vnpayTxnRef` (optional) |
| **Response** | `{ "success": true, "data": "https://sandbox.vnpayment.vn/..." }` |

| **API** | **API-58** |
|---|---|
| **Method** | GET |
| **Endpoint** | `/api/payments/vnpay/return` |
| **Auth** | Public |
| **Query Params** | `vnp_TxnRef*`, `vnp_ResponseCode*`, `vnp_TransactionStatus*`, `vnp_SecureHash*`, ... |
| **Response** | `{ "success": true, "data": { "status": "SUCCESS/FAILED", ... } }` |

| **API** | **API-59** |
|---|---|
| **Method** | POST |
| **Endpoint** | `/api/payments/vnpay/ipn` |
| **Auth** | Public |
| **Request Body** | `{ "vnp_TxnRef": "string", "vnp_ResponseCode": "string", "vnp_TransactionStatus": "string", "vnp_SecureHash": "string", ... }` |
| **Response** | `{ "success": true }` |

---

## 3.9 Reviews

| **API** | **API-63** |
|---|---|
| **Method** | POST |
| **Endpoint** | `/api/reviews` |
| **Auth** | CUSTOMER (userId từ JWT) |
| **Query Params** | `bookingId*` |
| **Request Body** | `{ "rating*": 5, "comment": "string" }` |
| **Response** | `{ "success": true, "data": { ... } }` |

| **API** | **API-65** |
|---|---|
| **Method** | PUT |
| **Endpoint** | `/api/reviews/{id}/reply` |
| **Auth** | OWNER |
| **Query Params** | `reply*` |
| **Response** | `{ "success": true, "data": { ... } }` |

---

## 3.10 Notifications

| **API** | **API-66** |
|---|---|
| **Method** | GET |
| **Endpoint** | `/api/notifications` |
| **Auth** | Authenticated (userId từ JWT) |
| **Query Params** | `page` (default: 0), `size` (default: 10) |
| **Response** | `{ "success": true, "data": { "content": [...], "totalPages": N } }` |

| **API** | **API-68** |
|---|---|
| **Method** | GET |
| **Endpoint** | `/api/notifications/unread-count` |
| **Auth** | Authenticated (userId từ JWT) |
| **Response** | `{ "success": true, "data": 5 }` |

| **API** | **API-69** |
|---|---|
| **Method** | PUT |
| **Endpoint** | `/api/notifications/{id}/read` |
| **Auth** | Authenticated (userId từ JWT) |
| **Response** | `{ "success": true, "data": { ... } }` |

---

## 3.11 Check-ins

| **API** | **API-71** |
|---|---|
| **Method** | POST |
| **Endpoint** | `/api/checkins` |
| **Auth** | OWNER |
| **Query Params** | `bookingId*`, `checkedByUserId*` |
| **Request Body** | CheckInRequest |
| **Response** | `{ "success": true, "data": { ... } }` |

| **API** | **API-72** |
|---|---|
| **Method** | GET |
| **Endpoint** | `/api/checkins/booking/{bookingId}` |
| **Auth** | Authenticated |
| **Response** | `{ "success": true, "data": { ... } }` |

---

## 3.12 Facility Images

| **API** | **API-29** |
|---|---|
| **Method** | GET |
| **Endpoint** | `/api/facility-images/facility/{facilityId}` |
| **Auth** | Public |
| **Response** | `{ "success": true, "data": [ { "id": 1, "imageUrl": "...", "sortOrder": 1 } ] }` |

| **API** | **API-31** |
|---|---|
| **Method** | POST |
| **Endpoint** | `/api/facility-images/facility/{facilityId}/bulk` |
| **Auth** | OWNER |
| **Request Body** | `{ "imageUrls*": ["url1", "url2"] }` |
| **Response** | `{ "success": true, "data": [ ... ] }` |

---

## 3.13 Upload & Admin

| **API** | **API-73** |
|---|---|
| **Method** | POST |
| **Endpoint** | `/api/upload` |
| **Auth** | Authenticated |
| **Request** | `multipart/form-data` — `file*` |
| **Response** | `{ "success": true, "data": "https://res.cloudinary.com/..." }` |

| **API** | **API-74** |
|---|---|
| **Method** | GET |
| **Endpoint** | `/api/admin/health` |
| **Auth** | Public |
| **Response** | `"OK"` |

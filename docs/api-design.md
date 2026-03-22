# SPORTS FACILITY BOOKING SYSTEM

**API Design Documentation**

| | |
|---|---|
| **Tài liệu** | API Design Documentation v1.0 |
| **Tổng số API** | 31 APIs chia thành 8 nhóm chức năng |
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
| **CUSTOMER** | JWT token với role = CUSTOMER | Đặt sân, hủy sân, đánh giá |
| **OWNER** | JWT token với role = OWNER, kiểm tra ownership | Quản lý sân, xác nhận booking, check-in |
| **ADMIN** | JWT token với role = ADMIN | Duyệt cơ sở, quản lý người dùng, báo cáo |
| **CUSTOMER / OWNER / ADMIN** | Bất kỳ role nào đã đăng nhập | Xem profile, thông báo |

---

# 2. Tổng Hợp Danh Sách API

*Tổng cộng 31 APIs chia thành 8 nhóm. Ký hiệu \* trong cột Request là trường bắt buộc.*

| **#** | **API ID** | **Method** | **Endpoint** | **Tên API** | **Auth** |
|---|---|---|---|---|---|
| **Authentication & User \[UC-01, UC-02, UC-03\]** | | | | | |
| 1 | **API-01** | **POST** | `/api/auth/register` | Đăng Ký Tài Khoản | **Public** |
| 2 | **API-02** | **POST** | `/api/auth/login` | Đăng Nhập | **Public** |
| 3 | **API-03** | **GET** | `/api/users/me` | Lấy Thông Tin Profile | **ADMIN** |
| 4 | **API-04** | **PUT** | `/api/users/me` | Cập Nhật Profile | **ADMIN** |
| **Sport Types (Loại Sân) \[FR-30, UC-14\]** | | | | | |
| 5 | **API-05** | **GET** | `/api/sport-types` | Danh Sách Loại Sân | **Public** |
| 6 | **API-06** | **POST** | `/api/sport-types` | Tạo Loại Sân Mới | **ADMIN** |
| 7 | **API-07** | **PUT** | `/api/sport-types/{id}` | Cập Nhật Loại Sân | **ADMIN** |
| **Facilities (Cơ Sở Thể Thao) \[UC-03, UC-04, UC-08, UC-12\]** | | | | | |
| 8 | **API-08** | **GET** | `/api/facilities` | Tìm Kiếm & Danh Sách Cơ Sở | **Public** |
| 9 | **API-09** | **GET** | `/api/facilities/{id}` | Chi Tiết Cơ Sở | **Public** |
| 10 | **API-10** | **POST** | `/api/facilities` | Tạo Cơ Sở Mới (Chủ Sân) | **OWNER** |
| 11 | **API-11** | **PUT** | `/api/facilities/{id}` | Cập Nhật Cơ Sở (Chủ Sân) | **OWNER** |
| 12 | **API-12** | **PATCH** | `/api/facilities/{id}/status` | Duyệt / Đình Chỉ Cơ Sở (Admin) | **ADMIN** |
| **Courts (Sân) \[UC-08, UC-09\]** | | | | | |
| 13 | **API-13** | **GET** | `/api/facilities/{facilityId}/courts` | Danh Sách Sân Theo Cơ Sở | **Public** |
| 14 | **API-14** | **POST** | `/api/facilities/{facilityId}/courts` | Tạo Sân Mới | **OWNER** |
| 15 | **API-15** | **PUT** | `/api/courts/{id}` | Cập Nhật Thông Tin Sân | **OWNER** |
| **Time Slots (Khung Giờ & Giá) \[UC-09\]** | | | | | |
| 16 | **API-16** | **GET** | `/api/courts/{courtId}/time-slots` | Danh Sách Khung Giờ Của Sân | **Public** |
| 17 | **API-17** | **POST** | `/api/courts/{courtId}/time-slots` | Tạo Khung Giờ & Giá | **OWNER** |
| 18 | **API-18** | **PUT** | `/api/time-slots/{id}` | Cập Nhật Khung Giờ | **OWNER** |
| **Bookings (Đặt Sân) \[UC-05, UC-06, UC-10, UC-11\]** | | | | | |
| 19 | **API-20** | **POST** | `/api/bookings` | Tạo Booking Mới | **CUST** |
| 20 | **API-21** | **GET** | `/api/bookings/my` | Lịch Sử Đặt Sân Của Tôi | **CUST** |
| 21 | **API-22** | **GET** | `/api/bookings/{id}` | Chi Tiết Booking | **ADMIN** |
| 22 | **API-23** | **PATCH** | `/api/bookings/{id}/cancel` | Hủy Booking | **CUST** |
| 23 | **API-24** | **PATCH** | `/api/bookings/{id}/confirm` | Xác Nhận Booking (Chủ Sân) | **OWNER** |
| 24 | **API-25** | **PATCH** | `/api/bookings/{id}/reject` | Từ Chối Booking (Chủ Sân) | **OWNER** |
| 25 | **API-26** | **POST** | `/api/bookings/{id}/checkin` | Check-In Khách (Chủ Sân) | **OWNER** |
| 26 | **API-27** | **GET** | `/api/facilities/{facilityId}/bookings` | Danh Sách Booking Của Cơ Sở | **OWNER** |
| **Reviews (Đánh Giá) \[UC-07\]** | | | | | |
| 27 | **API-30** | **POST** | `/api/reviews` | Tạo Đánh Giá | **CUST** |
| 28 | **API-31** | **GET** | `/api/facilities/{facilityId}/reviews` | Đánh Giá Của Cơ Sở | **Public** |
| **Admin Management \[UC-12, UC-13, UC-14\]** | | | | | |
| 29 | **API-33** | **GET** | `/api/admin/facilities` | Danh Sách Cơ Sở (Admin) | **ADMIN** |
| 30 | **API-34** | **GET** | `/api/admin/owners` | Danh Sách Chủ Sân (Admin) | **ADMIN** |

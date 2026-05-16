# TEST SUMMARY

## Tổng quan

Hệ thống Sport Facility Booking đã được kiểm thử bằng các phương pháp:

- Unit Test – Backend (Spring Boot / JUnit 5 + Mockito)
- Unit Test – Frontend (ReactJS / Jest + React Testing Library)
- Manual Test – Kỹ thuật hộp đen (Phân vùng tương đương, Giá trị biên, Bảng quyết định, Chuyển trạng thái)

Kết quả cho thấy hệ thống hoạt động ổn định, đáp ứng các yêu cầu chức năng chính.

---

## Unit Test

### Số liệu

| Tầng | Công cụ | Số test suite | Số test case | Pass rate | Coverage |
|------|---------|--------------|--------------|-----------|----------|
| Backend (Service + Controller + Entity) | Maven Surefire + JUnit 5 + Mockito + JaCoCo | 22 | **274** | **100%** | Đạt ngưỡng JaCoCo |
| Frontend (Service + Component + Context) | Jest + React Testing Library | 41 | **280** | **100%** | Stmts 82.66% / Lines 83.35% |

### Backend – Phân bổ theo tầng

| Tầng | File test | Số TC |
|------|-----------|-------|
| Service | BookingServiceTest, CheckInServiceTest, GetBookingServiceTest, NotificationServiceTest, PaymentServiceTest, ReviewServiceTest, UpdateBookingServiceTest, UserServiceTest | **125** |
| Controller | BookingControllerTest, CheckInControllerTest, NotificationControllerTest, PaymentControllerTest, ReviewControllerTest, TimeSlotControllerTest, UserControllerTest | **79** |
| Config / Entity / Other | JwtTokenProviderTest, FacilityApprovalLogTest, FacilityTest, GlobalExceptionHandlerTest, MapperTest, BookingSchedulerTest, SpringBootApplicationTest | **70** |
| **Tổng** | | **274** |

### Frontend – Phân bổ theo loại

| Loại | Số TC |
|------|-------|
| Service (API calls) | 71 |
| Component (UI rendering) | 195 |
| Context (AuthContext) | 3 |
| Legacy | 11 |
| **Tổng** | **280** |

---

## Manual Test

- **132 test case** được thiết kế theo 4 kỹ thuật hộp đen, tập trung vào các luồng nghiệp vụ chính.
- Các chức năng kiểm thử: Đăng ký / Đăng nhập, Đặt sân, Thanh toán VNPay, Đánh giá, Cấu hình khung giờ, Duyệt sân, Check-in, Quản lý tài khoản.

| Kỹ thuật | Số TC |
|----------|-------|
| Phân vùng tương đương (EP) | 54 |
| Phân tích giá trị biên (BVA) | 19 |
| Bảng quyết định (DT) | 25 |
| Chuyển trạng thái (ST) | 26 |
| **Tổng** | **124** |

---

## Kết luận

- Tổng số test case tự động: **554** (274 BE + 280 FE), tất cả **PASSED**.
- Độ phủ code đạt yêu cầu ở cả hai tầng.
- 124 test case manual đã được thiết kế dựa trực tiếp trên source code thực tế (service, DTO, entity).
- Hệ thống sẵn sàng cho việc trình bày và bảo vệ.

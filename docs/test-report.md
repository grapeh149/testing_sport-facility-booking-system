# TEST REPORT
# sport-facility-booking-system

---

## 1. Giới thiệu

Tài liệu này tổng hợp kết quả kiểm thử của hệ thống **Sport Facility Booking** – nền tảng đặt sân thể thao trực tuyến hỗ trợ 3 vai trò: Admin, Chủ sân (Owner) và Khách hàng (Customer).

Phạm vi kiểm thử bao gồm:

- **Unit Test – Backend:** Kiểm thử các lớp Service và Controller bằng JUnit 5 + Mockito
- **Unit Test – Frontend:** Kiểm thử các service, component và context bằng Jest + React Testing Library
- **Manual Test:** Thiết kế và thực thi test case theo kỹ thuật hộp đen

Mục tiêu nhằm đảm bảo các chức năng của hệ thống hoạt động đúng, ổn định và đáp ứng yêu cầu.

---

## 2. Unit Test Backend

### Công cụ

- **Framework:** Spring Boot 3.3.5 / Java 21
- **Test runner:** Maven Surefire 3.2.5 + JUnit Platform
- **Mocking:** Mockito (qua Spring MockMvc)
- **Coverage:** JaCoCo 0.8.12
- **Build tool:** Apache Maven

### Tổng số test case thực tế

**274 test case – 100% PASSED – BUILD SUCCESS**

### Các file test chính

| File test | Tầng | Chức năng kiểm thử | Số TC |
|---|---|---|---|
| `BookingServiceTest` | Service | Tạo booking, kiểm tra ngày, chống đặt trùng, tính tiền cọc | 19 |
| `GetBookingServiceTest` | Service | Lấy booking theo customer, owner, bookingCode, courtId | 19 |
| `UpdateBookingServiceTest` | Service | Xác nhận, hủy booking, auto-confirm scheduler | 21 |
| `PaymentServiceTest` | Service | Tạo URL VNPay, xử lý return/IPN, hoàn tiền | 45 |
| `CheckInServiceTest` | Service | Check-in khách hàng, kiểm tra quyền owner | 10 |
| `ReviewServiceTest` | Service | Tạo đánh giá (chỉ CHECKED_IN), cập nhật avg rating | 3 |
| `NotificationServiceTest` | Service | Gửi thông báo theo loại sự kiện | 4 |
| `UserServiceTest` | Service | Khóa / mở khóa tài khoản | 4 |
| `BookingControllerTest` | Controller | API tạo booking, lấy danh sách, xác nhận, hủy | 22 |
| `PaymentControllerTest` | Controller | API tạo URL VNPay, xử lý return/IPN | 10 |
| `ReviewControllerTest` | Controller | API tạo và lấy đánh giá | 9 |
| `TimeSlotControllerTest` | Controller | API CRUD khung giờ | 10 |
| `CheckInControllerTest` | Controller | API check-in | 4 |
| `NotificationControllerTest` | Controller | API đọc / đánh dấu thông báo | 11 |
| `UserControllerTest` | Controller | API CRUD user, lock/unlock | 13 |
| `FacilityApprovalLogTest` | Entity | Getter/setter FacilityApprovalLog | 21 |
| `FacilityTest` | Entity | Getter/setter Facility | 26 |
| `MapperTest` | Mapper | Mapping entity ↔ DTO | 12 |
| `JwtTokenProviderTest` | Config | Tạo và xác thực JWT token | 6 |
| `GlobalExceptionHandlerTest` | Exception | Xử lý RuntimeException → HTTP response | 3 |
| `BookingSchedulerTest` | Scheduler | Auto-confirm PENDING_CONFIRM booking mỗi đêm | 1 |
| `SportfacilitybookingApplicationTests` | Integration | Spring context load với DB thực (Azure SQL) | 1 |

### Kết quả

```
Tests run: 274, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
Total time: 55.221 s
```

| Chỉ số | Kết quả |
|--------|---------|
| Tổng test case | 274 |
| Passed | 274 (100%) |
| Failed | 0 |
| Errors | 0 |
| Skipped | 0 |
| JaCoCo coverage | Đạt ngưỡng 89% (All coverage checks have been met) |
| Build status | **SUCCESS** |

---

## 3. Unit Test Frontend

### Công cụ

- **Framework:** ReactJS (Create React App)
- **Test runner:** Jest
- **Testing library:** React Testing Library
- **Mock:** jest.fn(), jest.mock()

### Tổng số test case thực tế

**280 test case – 100% PASSED – 41 test suite**

### Các file test chính

| Nhóm | File test | Chức năng kiểm thử | Số TC |
|---|---|---|---|
| **Service** | `adminService.test.js` | Approve/reject facility, CRUD user, reports, dashboard stats | 17 |
| **Service** | `authService.test.js` | Register, login, getProfile, updateProfile, changePassword | 9 |
| **Service** | `bookingService.test.js` | CRUD booking, confirm, cancel, get by owner/customer | 10 |
| **Service** | `facilityService.test.js` | CRUD facility, search, get by owner | 8 |
| **Service** | `paymentService.test.js` | Tạo URL VNPay, lấy lịch sử thanh toán | 5 |
| **Service** | `notificationService.test.js` | Lấy và đánh dấu đã đọc thông báo | 4 |
| **Service** | `checkInService.test.js` | Gọi API check-in | 3 |
| **Service** | `timeslotService.test.js` | CRUD timeslot | 5 |
| **Service** | `userService.test.js` | CRUD user | 5 |
| **Service** | `sportTypeService.test.js` | Lấy danh sách loại sân | 3 |
| **Service** | `reviewService.test.js` | Tạo và lấy review | 2 |
| **Component** | `AdminDashboard.test.js` | Render dashboard, duyệt sân, quản lý user | 47 |
| **Component** | `BookingForm.test.js` | Form đặt sân, chọn ngày, khung giờ | 15 |
| **Component** | `CourtDetail.test.js` | Xem chi tiết sân, timeslot, booking | 12 |
| **Component** | `Login.test.js` | Form đăng nhập, validation, submit | 10 |
| **Component** | `Register.test.js` | Form đăng ký, validation | 8 |
| **Component** | `MyBookings.test.js` | Danh sách booking của customer | 12 |
| **Component** | `FacilityList.test.js` | Danh sách sân, lọc, tìm kiếm | 10 |
| **Component** | Các component khác | Navigation, PaymentReturn, ReviewModal, UserProfile, ... | 81 |
| **Context** | `AuthContext.test.js` | Login/logout state, token lưu localStorage | 3 |
| **Legacy** | Các file cũ | Smoke test / import check | 11 |

### Coverage Frontend

| Chỉ số | Giá trị |
|--------|---------|
| Statements | **82.66%** |
| Branch | **65.62%** |
| Functions | **78.50%** |
| Lines | **83.35%** |

### Kết quả

```
Test Suites: 41 passed, 41 total
Tests:       280 passed, 280 total
Time:        14.844 s
```

| Chỉ số | Kết quả |
|--------|---------|
| Tổng test suite | 41 |
| Tổng test case | 280 |
| Passed | 280 (100%) |
| Failed | 0 |

---

## 4. Manual Test

### Phương pháp

Thiết kế test case thủ công theo **4 kỹ thuật hộp đen**, dựa trực tiếp trên source code (service, DTO, entity) của hệ thống:

| Kỹ thuật | Mô tả | Số TC |
|----------|-------|-------|
| Phân vùng tương đương (EP) | Phân lớp đầu vào hợp lệ / không hợp lệ cho Register, Login, Booking, Review, TimeSlot | 54|
| Phân tích giá trị biên (BVA) | Kiểm thử giá trị biên của rating (1–5), comment (rỗng/1 ký tự), ngày đặt (hôm nay/quá khứ), overlap timeslot | 19 |
| Bảng quyết định (DT) | Tổ hợp điều kiện cho Login (3 điều kiện), Duyệt sân, Hủy booking, Check-in | 21 |
| Chuyển trạng thái (ST) | Vòng đời Booking (PENDING_PAYMENT→CONFIRMED→CHECKED_IN), Facility (PENDING→ACTIVE/REJECTED), User (ACTIVE/LOCKED) | 26 |
| **Tổng** | | **120** |

### Chức năng được kiểm thử

| Chức năng | Kỹ thuật áp dụng |
|-----------|-----------------|
| Đăng ký tài khoản | EP, BVA |
| Đăng nhập | EP, DT |
| Đặt sân (Booking) | EP, BVA, ST |
| Gửi đánh giá (Review) | EP, BVA |
| Cấu hình khung giờ (TimeSlot) | EP, BVA |
| Admin duyệt / từ chối sân | DT, ST |
| Hủy booking | DT, ST |
| Check-in khách hàng | DT, ST |
| Quản lý tài khoản (Lock/Unlock) | ST |

### Một số điểm phát hiện từ thiết kế test case

| Phát hiện | Loại | Chi tiết |
|-----------|------|---------|
| Không có min-length cho password | Thiếu validation | `UserRegistrationRequest` chỉ có `@NotEmpty`, không có `@Size(min=6)` |
| Không có format validation cho phone | Thiếu validation | Chỉ `@NotEmpty`, mọi chuỗi ký tự đều được chấp nhận |
| `cancelBooking()` không kiểm tra trạng thái booking | Rủi ro logic | Service set CANCELLED bất kể trạng thái hiện tại (COMPLETED, CHECKED_IN...) |
| `confirmBooking()` không kiểm tra trạng thái booking | Rủi ro logic | Service set CONFIRMED từ bất kỳ trạng thái nào |
| Không có max-length cho comment (Review) | Thiếu validation | Chỉ `@NotEmpty`, giới hạn thực tế phụ thuộc DB column |

---

## 5. Kết luận

Hệ thống Sport Facility Booking đã được kiểm thử toàn diện qua 3 tầng:

| Phương pháp | Số TC | Kết quả |
|-------------|-------|---------|
| Unit Test Backend (Spring Boot / JUnit 5) | 274 | ✅ 100% PASSED |
| Unit Test Frontend (ReactJS / Jest) | 280 | ✅ 100% PASSED |
| Manual Test (Hộp đen) | 120 | Đã thiết kế – sẵn sàng thực thi |
| **Tổng** | **674** | |

Các chỉ số đạt và vượt yêu cầu môn học. Qua quá trình thiết kế test case hộp đen, nhóm đã phát hiện một số điểm thiếu validation trong service layer có thể dẫn đến rủi ro logic nghiệp vụ – đây là giá trị thực tế của kiểm thử hộp đen bên cạnh unit test tự động.

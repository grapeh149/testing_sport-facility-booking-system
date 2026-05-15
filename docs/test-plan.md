# TEST PLAN  
## Hệ Thống Đặt Sân Thể Thao (Sport Facility Booking System)

---

# 1. Giới thiệu

## 1.1 Mục đích
Tài liệu này mô tả kế hoạch kiểm thử cho hệ thống đặt sân thể thao nhằm đảm bảo các chức năng nghiệp vụ hoạt động chính xác, ổn định và đáp ứng yêu cầu trước khi triển khai thực tế.

## 1.2 Phạm vi kiểm thử
Bao gồm:
- Backend REST API
- Frontend Web Application
- Database
- Authentication & Authorization
- Booking Workflow
- Deposit Payment Workflow
- Owner/Admin Management

Không bao gồm:
- Mobile Application
- Kiểm thử bảo mật chuyên sâu
- Kiểm thử tải quy mô lớn
- Kiểm thử nội bộ của cổng thanh toán bên thứ ba

---

# 2. Mục tiêu kiểm thử

- Xác minh các chức năng hoạt động đúng yêu cầu
- Đảm bảo logic nghiệp vụ chính xác
- Kiểm tra tính đúng đắn của API response
- Đảm bảo phân quyền hoạt động chính xác
- Phát hiện lỗi nghiêm trọng trước khi release
- Đảm bảo dữ liệu không bị sai lệch hoặc mất mát

---

# 3. Đối tượng kiểm thử

| Module | Mô tả |
|---|---|
| Authentication | Đăng nhập, đăng ký, JWT |
| Booking Management | Đặt sân và quản lý booking |
| Time Slot Management | Quản lý khung giờ và giá |
| Deposit Payment | Thanh toán đặt cọc |
| Booking Approval | Xác nhận / từ chối booking |
| Check-in | Check-in khách hàng |
| Court Management | Quản lý sân |
| Court Type Management | Quản lý loại sân |
| Admin Management | Quản trị hệ thống |

---

# 4. Chiến lược kiểm thử

## 4.1 Cấp độ kiểm thử

| Cấp độ | Mô tả |
|---|---|
| Unit Testing | Kiểm thử hàm và component riêng lẻ |
| Integration Testing | Kiểm thử tương tác giữa các module |
| System Testing | Kiểm thử toàn hệ thống |
| API Testing | Kiểm thử REST API |
| User Acceptance Testing | Kiểm thử theo góc nhìn người dùng |

---

## 4.2 Loại kiểm thử

| Loại kiểm thử | Mô tả |
|---|---|
| Functional Testing | Kiểm tra chức năng |
| Validation Testing | Kiểm tra dữ liệu đầu vào |
| Authorization Testing | Kiểm tra phân quyền |
| Error Handling Testing | Kiểm tra xử lý lỗi |
| Boundary Testing | Kiểm tra giá trị biên |
| State Transition Testing | Kiểm tra chuyển trạng thái |

---

## 4.3 Kỹ thuật thiết kế test case

| Kỹ thuật | Áp dụng |
|---|---|
| Equivalence Partitioning (EP) | Validation input |
| Boundary Value Analysis (BVA) | Giá, ngày, thời gian |
| Decision Table Testing | Payment và Authorization |
| State Transition Testing | Booking workflow |

---

# 5. Môi trường kiểm thử

| Thành phần | Công nghệ |
|---|---|
| Frontend | ReactJS |
| Backend | Spring Boot REST API |
| Database | Azure SQL |
| API Testing | Postman |
| Backend Testing | JUnit + Mockito |
| Frontend Testing | Jest + React Testing Library |
| Version Control | Git + GitHub |

---

# 6. Nhân sự & trách nhiệm

| Thành viên | Trách nhiệm |
|---|---|
| Hoàng Thái Huy (PM) | Phân tích yêu cầu, lập kế hoạch, báo cáo |
| Hoàng Bảo Châu | Backend development & testing |
| Nguyễn Ngọc Phú | Frontend implementation & testing |
| Bùi Văn Đức | Database, integration, test execution |

---

# 7. Lịch trình kiểm thử

| Giai đoạn | Thời gian | Nội dung |
|---|---|---|
| Phân tích & Thiết kế | Week 1–2 | Phân tích yêu cầu và thiết kế hệ thống |
| Backend & Database | Week 3–7 | Phát triển API và database |
| Frontend Development | Week 7–8 | Phát triển giao diện |
| Testing & Bug Fixing | Week 8–9 | Testing và sửa lỗi |
| Final Review | Week 10 | Tổng hợp và đánh giá cuối |

---

# 8. Điều kiện bắt đầu & kết thúc kiểm thử

## 8.1 Điều kiện bắt đầu
- Hệ thống build thành công
- Database được khởi tạo
- API hoạt động ổn định
- Có dữ liệu test cơ bản
- Môi trường test sẵn sàng

## 8.2 Điều kiện kết thúc
- Không còn lỗi Critical/Blocker
- Tỷ lệ pass >= 80%
- Core workflow hoạt động đúng
- Test report được hoàn thành

---

# 9. Điều kiện tạm ngưng & tiếp tục

## 9.1 Tạm ngưng
- Server hoặc database bị lỗi
- API core bị crash
- Môi trường test không ổn định
- Thiếu dữ liệu test quan trọng

## 9.2 Tiếp tục
- Environment ổn định trở lại
- Blocker bug đã được fix
- Deploy bản build mới thành công

---

# 10. Mức độ nghiêm trọng của lỗi

| Severity | Mô tả |
|---|---|
| Critical | Crash hệ thống hoặc mất dữ liệu |
| High | Sai logic nghiệp vụ chính |
| Medium | Sai chức năng nhưng có workaround |
| Low | Lỗi nhỏ UI hoặc validation |

---

# 11. Tiêu chí Pass / Fail

## Pass
- Kết quả thực tế khớp expected result
- API trả đúng status code
- Không có lỗi nghiêm trọng
- Dữ liệu được lưu chính xác

## Fail
- Sai logic nghiệp vụ
- API response sai cấu trúc
- Cho phép truy cập trái quyền
- Mất hoặc sai dữ liệu

---

# 12. Deliverables

| Deliverable | Mô tả |
|---|---|
| Test Plan | Kế hoạch kiểm thử |
| Test Cases | Danh sách test case |
| Test Data | Dữ liệu kiểm thử |
| Defect Report | Báo cáo lỗi |
| Test Summary Report | Báo cáo kết quả kiểm thử |

---

# 13. Rủi ro & giảm thiểu

| Rủi ro | Giải pháp |
|---|---|
| Environment không ổn định | Chuẩn bị backup environment |
| Thiếu test data | Seed dữ liệu mẫu |
| Trễ tiến độ | Ưu tiên core workflow |
| Conflict integration | Test integration thường xuyên |


---

# 14. Kết luận

Tài liệu Test Plan này được xây dựng dựa trên cấu trúc kiểm thử theo chuẩn quốc tế và định hướng IEEE nhằm đảm bảo hệ thống đặt sân thể thao được kiểm thử đầy đủ trước khi triển khai. Quá trình kiểm thử tập trung vào tính đúng đắn của nghiệp vụ, độ ổn định của hệ thống, tính nhất quán dữ liệu và khả năng phân quyền giữa các vai trò người dùng.
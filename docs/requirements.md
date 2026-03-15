# Hệ thống đặt sân thể thao (Sport Facility Booking)

## 1. Đăng nhập & phân quyền tài khoản

Hệ thống hỗ trợ 3 vai trò gồm **Quản trị viên (Admin)**, **Chủ sân (Owner)** và **Khách hàng (Customer)**.

- **Quản trị viên (Admin)**: Quản lý toàn bộ hệ thống và duyệt các sân mới đăng ký.
- **Chủ sân (Owner)**: Quản lý các sân của mình, cấu hình khung giờ hoạt động và giá thuê, đồng thời xử lý các yêu cầu đặt sân từ khách hàng.
- **Khách hàng (Customer)**: Tìm kiếm sân, xem lịch trống, đặt sân và thanh toán đặt cọc.

Hệ thống áp dụng **cơ chế phân quyền theo vai trò (Role-Based Access Control)** để đảm bảo người dùng chỉ có thể truy cập các chức năng được phép.

---

## 2. Tìm kiếm và xem thông tin sân thể thao

Khách hàng có thể tìm kiếm sân thể thao dựa trên:
- Vị trí (Chi nhánh trong hệ thống sân)
- Loại sân (cầu lông, pickleball, tennis...)

Hệ thống hiển thị danh sách các sân phù hợp bao gồm các thông tin:
- Tên sân
- Địa chỉ
- Loại sân
- Giá thuê theo khung giờ
- Trạng thái hoạt động

Khách hàng có thể xem chi tiết thông tin sân và lịch trống trước khi đặt sân.

---

## 3. Xem lịch trống và đặt sân

Khách hàng có thể xem **lịch trống của sân** theo ngày và khung giờ.

Sau khi chọn được khung giờ phù hợp, khách hàng có thể gửi **yêu cầu đặt sân trực tuyến**.

Yêu cầu đặt sân có thể có các trạng thái sau:
- **Pending** – đang chờ chủ sân xác nhận
- **Confirmed** – đặt sân đã được chấp nhận
- **Cancelled** – đặt sân bị hủy hoặc bị từ chối
- **Completed** – đặt sân đã hoàn thành sau khi sử dụng

---

## 4. Thanh toán đặt cọc

Sau khi gửi yêu cầu đặt sân, khách hàng có thể **thanh toán tiền đặt cọc** để giữ sân.

Hệ thống sẽ ghi nhận thông tin giao dịch và cập nhật trạng thái thanh toán tương ứng.

Nếu khách hàng không thanh toán trong thời gian quy định, yêu cầu đặt sân có thể **bị hủy tự động**.

---

## 5. Cấu hình khung giờ và giá thuê

Chủ sân có thể cấu hình:
- **Các khung giờ hoạt động**
- **Giá thuê cho từng khung giờ**

Giá thuê có thể thay đổi tùy theo:
- Giờ cao điểm
- Giờ bình thường
- Loại sân

Hệ thống sử dụng các cấu hình này để tính toán giá thuê khi khách hàng đặt sân.

---

## 6. Xác nhận hoặc từ chối yêu cầu đặt sân

Chủ sân có thể xem các yêu cầu đặt sân do khách hàng gửi.

Chủ sân có thể:
- **Xác nhận (Confirm)** nếu sân còn trống
- **Từ chối (Reject)** nếu có xung đột hoặc vấn đề phát sinh

Sau khi xác nhận, hệ thống sẽ cập nhật lịch sân để **tránh việc đặt trùng khung giờ**.

---

## 7. Check-in khách hàng

Khi khách hàng đến sân vào thời gian đã đặt, chủ sân có thể thực hiện **check-in khách hàng**.

Tính năng này giúp:
- Xác nhận khách hàng đã sử dụng sân
- Lưu lại lịch sử sử dụng
- Hỗ trợ cho việc thống kê và báo cáo

---

## 8. Đăng ký sân mới

Chủ sân có thể đăng ký sân thể thao mới bằng cách cung cấp các thông tin như:
- Tên sân
- Địa chỉ
- Loại sân
- Mô tả

**Admin phải duyệt** sân trước khi sân được hiển thị cho khách hàng trên hệ thống.

---

## 9. Quản lý chủ sân

Admin có thể quản lý tài khoản chủ sân thông qua các thao tác **CRUD**:
- Tạo tài khoản mới
- Cập nhật thông tin chủ sân
- Khóa hoặc mở khóa tài khoản khi cần thiết

---

## 10. Quản lý loại sân

Admin quản lý **các danh mục loại sân thể thao**, ví dụ:
- Cầu lông
- Pickleball
- Tennis

Các danh mục này được sử dụng để phân loại sân và hỗ trợ chức năng tìm kiếm.

---

## 11. Tính năng nổi bật của hệ thống

Hệ thống có các cơ chế giúp đảm bảo tính toàn vẹn dữ liệu và tránh xung đột:

- Ngăn chặn **đặt trùng khung giờ** cho cùng một sân
- Đảm bảo **xác thực thanh toán đặt cọc** trước khi xác nhận đặt sân
- Lưu trữ **lịch sử đặt sân và lịch sử thanh toán**
# TEST PLAN — HỆ THỐNG ĐẶT SÂN THỂ THAO (SPORT FACILITY BOOKING)

---

## 1. Lịch trình & Tiến độ (Schedule / Milestones)

| Giai đoạn | Thời gian | Nội dung |
|-----------|-----------|----------|
| Phân tích & Thiết kế | Week 1–2 | Phân tích yêu cầu và thiết kế hệ thống |
| Backend & Database | Week 3–7 | Phát triển Database và Backend API, chuẩn bị chiến lược Testing |
| Frontend | Week 7–8 | Phát triển Frontend (React UI, Booking Interface) |
| Testing & Fix | Week 8–9 | Testing và sửa lỗi |

---

## 2. Nguồn lực & Nhân sự (Resources & Responsibilities)

### Hoàng Thái Huy — Project Manager (PM)
- Phân tích yêu cầu và xây dựng tài liệu đặc tả hệ thống
- Lập kế hoạch, theo dõi tiến độ và điều phối công việc nhóm
- Thiết kế kiến trúc hệ thống và phê duyệt giải pháp kỹ thuật
- Viết báo cáo tuần và tài liệu cuối dự án

### Hoàng Bảo Châu · Nguyễn Ngọc Phú · Bùi Văn Đức
- Thiết kế Database và xây dựng Backend API
- Phát triển Frontend (React UI, Booking Interface)
- Viết Unit Test và Integration Test cho các module phát triển
- Phối hợp sửa lỗi, review code và hỗ trợ nhau trong suốt dự án

---

## 3. Tiêu chí Pass / Fail & Tạm ngưng (Pass / Fail & Suspension Criteria)

###  Pass
- Tất cả test case chạy đúng kết quả mong đợi (expected output)
- Không có lỗi nghiêm trọng (Critical / Blocker bug) còn tồn tại
- Tỷ lệ test case pass đạt **≥ 85%** trên toàn bộ module
- Các API trả về đúng HTTP status code và cấu trúc response
- Ràng buộc nghiệp vụ hoạt động đúng: không đặt trùng giờ, không thanh toán cọc khi chưa Confirmed…

###  Fail
- Xuất hiện lỗi Blocker hoặc Critical chưa được xử lý
- Tỷ lệ test case pass dưới 85%
- Tính năng core bị sai logic: đặt sân, thanh toán, xác nhận
- API trả về dữ liệu sai hoặc crash không có thông báo lỗi rõ ràng
- Dữ liệu bị corrupt hoặc mất sau khi thực hiện thao tác

###  Tạm ngưng kiểm thử (Suspension)
- Môi trường test bị lỗi không thể khởi động (server down, DB không kết nối được)
- Có bug Blocker ảnh hưởng toàn bộ luồng, cần fix trước khi test tiếp
- Thiếu dữ liệu test cơ bản (không có tài khoản, không có sân mẫu)
- Phát hiện lỗi hệ thống nghiêm trọng ảnh hưởng toàn bộ nhóm tính năng

###  Tiếp tục kiểm thử (Resumption)
- Bug Blocker đã được fix và deploy lên môi trường test
- Môi trường test hoạt động ổn định trở lại
- Dữ liệu test đã được chuẩn bị đầy đủ

---

# TEST CASE DESIGN

> Hệ thống đặt sân thể thao — Kiểm thử hộp đen

---

## Module 1 — Cấu hình khung giờ và giá

 **Kỹ thuật:** Phân vùng tương đương (EP) + Phân tích giá trị biên (BVA) + Bảng quyết định

 **Lý do:** Có nhiều trường nhập liệu số (giờ, giá) cần kiểm tra biên, và logic 'có được phép sửa/xóa không' phụ thuộc vào trạng thái booking → phù hợp bảng quyết định.

| TC# | Mô tả | Input | Expected |
|-----|-------|-------|----------|
| TC01 | Tạo khung giờ hợp lệ | GioBatDau=08:00, GioKetThuc=10:00, GiaTien=150000 | Tạo thành công |
| TC02 | GioBatDau ≥ GioKetThuc | 10:00 → 08:00 | Báo lỗi, không tạo |
| TC03 | GioBatDau = GioKetThuc | 08:00 → 08:00 | Báo lỗi |
| TC04 | Trùng khung giờ cùng sân | Sân A đã có 08:00–10:00, tạo thêm 09:00–11:00 | Báo lỗi trùng giờ |
| TC05 | GiaTien = 0 | GiaTien=0 | Báo lỗi, không cho lưu |
| TC06 | GiaTien âm | GiaTien=-50000 | Báo lỗi |
| TC07 | Sửa giá khi có booking Confirmed | Booking tồn tại, sửa GiaTien | Cho sửa, TotalPrice booking cũ không đổi |
| TC08 | Xóa khung giờ khi có booking Pending | Xóa Time_Slot đang có booking Pending | Báo lỗi, không cho xóa |
| TC09 | Xóa khung giờ khi không có booking | Không có booking nào liên quan | Xóa thành công |

> **Loại bỏ (~6 TC):** Giá trị GiaTien rất lớn, định dạng giờ sai kiểu, tạo nhiều khung giờ liên tiếp không trùng. Framework validate tự xử lý.

---

## Module 2 — Xem lịch trống và đặt sân

 **Kỹ thuật:** Dịch chuyển trạng thái (State Transition) + Phân vùng tương đương (EP) + Phân tích giá trị biên (BVA)

 **Lý do:** Booking có vòng đời trạng thái rõ ràng (Pending → Confirmed/Rejected → Completed). Biên thời gian (quá khứ, hiện tại, giới hạn 30 ngày) là điểm dễ sai nhất.

| TC# | Mô tả | Input | Expected |
|-----|-------|-------|----------|
| TC10 | Đặt khung giờ còn trống | MaSan hợp lệ, MaGio trống, BookingDate hợp lệ | Tạo booking Status=Pending |
| TC11 | Đặt khung giờ đã Confirmed | Cùng MaSan+MaGio+BookingDate đã Confirmed | Báo lỗi đã có người đặt |
| TC12 | Đặt khung giờ đang Pending | Cùng slot đang Pending | Báo lỗi, không cho đặt |
| TC13 | Đặt slot đã Rejected | Slot có booking Rejected | Cho đặt bình thường |
| TC14 | Đặt BookingDate trong quá khứ | BookingDate = hôm qua | Báo lỗi không được đặt quá khứ |
| TC15 | Đặt hôm nay nhưng giờ đã qua | BookingDate=today, GioBatDau đã qua | Báo lỗi |
| TC16 | Đặt vượt quá 30 ngày tới | BookingDate = hôm nay + 31 ngày | Báo lỗi vượt giới hạn |
| TC17 | Đặt đúng ngày giới hạn 30 ngày | BookingDate = hôm nay + 30 ngày | Cho đặt |
| TC18 | Cùng khách đặt 2 sân trùng giờ | KH A đặt 2 sân khác nhau cùng MaGio+Date | Báo lỗi trùng lịch |
| TC19 | Đặt sân đang Inactive | SanTheThao.Status=Inactive | Báo lỗi sân không hoạt động |
| TC20 | TotalPrice tính đúng tại thời điểm đặt | GiaTien=200k lúc đặt, sau đó sửa lên 300k | TotalPrice booking = 200,000 |

> **Loại bỏ (~8 TC):** Đặt với MaKH không tồn tại (auth đã chặn trước), đặt nhiều ngày liên tiếp hợp lệ, kiểm tra hiển thị UI grid lịch. Thuộc tầng auth hoặc UI.

---

## Module 3 — Thanh toán đặt cọc (Deposit Payment)

 **Kỹ thuật:** Bảng quyết định (Decision Table) + Phân tích giá trị biên (BVA) + Dịch chuyển trạng thái (State Transition)

 **Lý do:** Quyết định 'có được phép thanh toán không' phụ thuộc tổ hợp trạng thái booking + cọc → bảng quyết định. Số tiền cọc có biên rõ ràng (0 đến TotalPrice). Bổ sung xử lý lỗi giao dịch, hoàn tiền và các trạng thái trung gian của payment.

| TC# | Mô tả | Input | Expected |
|-----|-------|-------|----------|
| TC21 | Thanh toán cọc thành công | Status=Confirmed, TienCoc=50000, PhuongThucTT=BankTransfer | ThongTinDatCoc tạo thành công, DepositStatus=Paid, booking vẫn Confirmed |
| TC22 | Thanh toán khi booking Pending | Status=Pending | Báo lỗi chưa được xác nhận (HTTP 400) |
| TC23 | Thanh toán khi booking Rejected | Status=Rejected | Báo lỗi booking không hợp lệ |
| TC24 | Thanh toán khi booking Cancelled | Status=Cancelled | Báo lỗi: "Booking đã bị hủy, không thể thanh toán" |
| TC25 | Thanh toán khi booking CheckedIn | Status=CheckedIn | Báo lỗi: "Booking đã hoàn thành check-in" |
| TC26 | TienCoc = 0 | TienCoc=0 | Báo lỗi |
| TC27 | TienCoc âm hoặc sai kiểu | TienCoc=-1 hoặc TienCoc="abc" | Báo lỗi validation |
| TC28 | TienCoc = 1 (biên dưới hợp lệ) | TienCoc=1 | Cho phép |
| TC29 | TienCoc = TotalPrice - 1 (biên trên hợp lệ) | TienCoc=TotalPrice-1 | Cho phép |
| TC30 | TienCoc = TotalPrice (cọc full) | TienCoc=TotalPrice | Cho phép |
| TC31 | TienCoc > TotalPrice | TienCoc=TotalPrice+1 | Báo lỗi vượt tổng tiền |
| TC32 | Thanh toán cọc lần 2 / race condition | 2 request thanh toán đồng thời cho cùng MaDatSan | Chỉ 1 thành công, còn lại báo lỗi đã thanh toán |
| TC33 | Giao dịch bị timeout / lỗi kết nối | Simulate network timeout khi gọi API thanh toán | Báo lỗi timeout, không tạo bản ghi, trạng thái booking không đổi |
| TC34 | Giao dịch thất bại phía cổng thanh toán | Payment gateway trả về FAILED | DepositStatus=Failed, booking vẫn Confirmed, cho phép thử lại |
| TC35 | Thử lại thanh toán sau khi Failed | DepositStatus=Failed trước đó, thực hiện lại | Tạo ThongTinDatCoc mới, DepositStatus=Paid |
| TC36 | Hoàn tiền khi chủ sân từ chối sau khi đã cọc | Owner Reject booking có DepositStatus=Paid | TrangThaiHoan=Pending, SoTienHoan=TienCoc |
| TC37 | Hoàn tiền khi khách hủy trước thời hạn | KH hủy booking trong thời gian hoàn tiền | TrangThaiHoan=Pending, SoTienHoan=100% TienCoc |
| TC38 | Hoàn tiền khi khách hủy sau thời hạn | KH hủy booking sau deadline | TrangThaiHoan=NoRefund, SoTienHoan=0 |
| TC39 | Admin xác nhận hoàn tiền | Admin xử lý TrangThaiHoan=Pending | TrangThaiHoan=Completed, lưu NgayHoan, SoTienDaHoan |
| TC40 | Phương thức thanh toán không hợp lệ | PhuongThucTT="CASH_INVALID" | Báo lỗi: phương thức không được hỗ trợ |
| TC41 | API trả về đúng cấu trúc response | POST /api/deposit body hợp lệ | HTTP 201, body có: depositId, MaDatSan, TienCoc, DepositStatus, NgayThanhToan |
| TC42 | Lịch sử thanh toán trả về đúng | MaDatSan có 1 Paid + 2 Failed | Trả về đủ 3 bản ghi với đúng trạng thái |

> **Loại bỏ (~9 TC):** Kiểm tra UI popup xác nhận, animation loading, email/SMS notification template, phương thức thanh toán qua dropdown UI, stress test UI nhiều lần liên tiếp. Thuộc tầng UI/integration, không ảnh hưởng logic nghiệp vụ core.

---

## Module 4 — Xác nhận / Từ chối đặt sân

 **Kỹ thuật:** Dịch chuyển trạng thái (State Transition) + Bảng quyết định (Decision Table)

 **Lý do:** Nghiệp vụ thuần về chuyển trạng thái. Mỗi hành động chỉ hợp lệ từ một số trạng thái nhất định. State: Pending → Confirmed/Rejected/CancelledByCustomer → CheckedIn/Cancelled.

| TC# | Mô tả | Input | Expected |
|-----|-------|-------|----------|
| TC30 | Xác nhận booking Pending hợp lệ | Status=Pending, slot còn trống | Status → Confirmed |
| TC31 | Từ chối booking Pending | Status=Pending | Status → Rejected, slot giải phóng |
| TC32 | Xác nhận booking đã khách hủy | Status=CancelledByCustomer | Báo lỗi booking không còn hợp lệ |
| TC33 | Từ chối booking đã cọc Paid | Status=Pending, có cọc Paid | Từ chối được, flag hoàn tiền |
| TC34 | Timeout: Pending quá giờ quy định | ExpiredAt đã qua | Hệ thống tự hủy → Status=Cancelled |
| TC35 | Undo xác nhận nhầm trong 5 phút | Confirmed → undo trong window 5 phút | Cho phép đổi về Pending |
| TC36 | Undo xác nhận sau 5 phút | Confirmed → undo sau 5 phút | Báo lỗi hết thời gian undo |
| TC37 | Xác nhận sân không thuộc mình | Chủ sân B thao tác booking của sân A | Báo lỗi không có quyền |

> **Loại bỏ (~4 TC):** Xác nhận nhiều booking cùng lúc (batch), filter danh sách pending theo ngày. Không ảnh hưởng tính đúng đắn nghiệp vụ core.

---

## Module 5 — Check-in khách hàng

 **Kỹ thuật:** Dịch chuyển trạng thái (State Transition) + Phân vùng tương đương (EP)

 **Lý do:** Check-in chỉ hợp lệ từ trạng thái Confirmed và trong khung thời gian phù hợp.

| TC# | Mô tả | Input | Expected |
|-----|-------|-------|----------|
| TC38 | Check-in booking Confirmed đúng giờ | Status=Confirmed, đúng ngày+giờ | Status → CheckedIn |
| TC39 | Check-in booking Pending | Status=Pending | Báo lỗi chưa xác nhận |
| TC40 | Check-in booking Cancelled | Status=Cancelled | Báo lỗi booking không hợp lệ |
| TC41 | Check-in sai ngày (booking ngày mai) | BookingDate = tomorrow | Báo lỗi chưa đến giờ |
| TC42 | Check-in sai sân (nhầm MaSan) | MaSan không khớp booking | Báo lỗi không tìm thấy booking |
| TC43 | Check-in lần 2 cùng booking | Status đã CheckedIn | Báo lỗi đã check-in rồi |

> **Loại bỏ (~3 TC):** Check-in bằng QR code (chưa có tính năng), tìm kiếm booking theo tên khách. Không thuộc scope MVP.

---

## Module 6 — Duyệt sân mới (Admin)

 **Kỹ thuật:** Dịch chuyển trạng thái (State Transition) + Bảng quyết định (Decision Table)

 **Lý do:** Sân có vòng đời Pending → Active/Rejected. Admin là người duy nhất có quyền chuyển trạng thái này.

| TC# | Mô tả | Input | Expected |
|-----|-------|-------|----------|
| TC44 | Duyệt sân đang Pending | Status=Pending | Status → Active, sân hiển thị cho khách |
| TC45 | Từ chối sân đang Pending | Status=Pending | Status → Rejected, không hiển thị |
| TC46 | Duyệt lại sân đã Active | Status=Active | Báo lỗi hoặc bỏ qua |
| TC47 | Sân Active hiển thị cho khách | Status → Active vừa duyệt | Sân xuất hiện trong kết quả tìm kiếm |
| TC48 | Sân Rejected không hiển thị | Status=Rejected | Không xuất hiện khi khách tìm kiếm |

> **Loại bỏ (~3 TC):** Duyệt hàng loạt nhiều sân, lọc sân theo loại trong trang admin. Không ảnh hưởng luồng duyệt core.

---

## Module 7 — Quản lý loại sân (Admin)

 **Kỹ thuật:** Phân vùng tương đương (EP) + Dịch chuyển trạng thái (IsActive)

 **Lý do:** Loại sân có trạng thái Active/Inactive ảnh hưởng đến khả năng tạo sân mới và tìm kiếm. Cần kiểm tra vùng hợp lệ/không hợp lệ cho TenLoaiSan.

| TC# | Mô tả | Input | Expected |
|-----|-------|-------|----------|
| TC49 | Thêm loại sân mới hợp lệ | TenLoaiSan="Pickleball" mới | Thêm thành công |
| TC50 | Thêm loại sân trùng tên | TenLoaiSan đã tồn tại | Báo lỗi trùng |
| TC51 | Tắt loại sân (soft delete) | IsActive=true → false | IsActive=false |
| TC52 | Tạo sân mới với loại Inactive | MaLoaiSan của loại Inactive | Báo lỗi loại sân không hoạt động |
| TC53 | Khách không thấy loại Inactive | Filter tìm kiếm | Loại Inactive không xuất hiện |
| TC54 | Sân cũ thuộc loại Inactive vẫn hoạt động | Sân tạo trước khi tắt loại | Sân vẫn Active, booking vẫn chạy |
| TC55 | Bật lại loại sân đã Inactive | IsActive=false → true | Loại xuất hiện lại trong filter |

> **Loại bỏ (~4 TC):** Sắp xếp danh sách loại sân, phân trang, tìm kiếm loại sân trong admin. Không ảnh hưởng tính đúng đắn dữ liệu.

---

## Tổng kết

| Nghiệp vụ | TC giữ lại | TC loại bỏ | Kỹ thuật chính |
|-----------|-----------|-----------|----------------|
| Cấu hình khung giờ & giá | 9 | 6 | EP + BVA + Decision Table |
| Đặt sân | 11 | 8 | State Transition + EP + BVA |
| Thanh toán đặt cọc | 22 | 9 | Decision Table + BVA + State Transition |
| Xác nhận / Từ chối | 8 | 4 | State Transition + Decision Table |
| Check-in | 6 | 3 | State Transition + EP |
| Duyệt sân | 5 | 3 | State Transition + Decision Table |
| Quản lý loại sân | 7 | 4 | EP + State Transition |
| **Tổng** | **68** | **37** | |

> Tổng 37 test case loại bỏ đều rơi vào 3 nhóm: validate tầng framework tự xử lý, UI/UX không ảnh hưởng dữ liệu, và tính năng ngoài scope MVP hiện tại.

---

## Tổng số TC theo mức độ ưu tiên

> **Định nghĩa mức độ ưu tiên:**  
>    🔴 **High** — Liên quan trực tiếp đến luồng nghiệp vụ core, nếu sai sẽ block toàn bộ hệ thống  
>    🟡 **Medium** — Ảnh hưởng đến trải nghiệm hoặc tính đúng đắn dữ liệu nhưng không block hoàn toàn  
>    🟢 **Low** — Trường hợp biên, edge case ít xảy ra trong thực tế  

| Module | 🔴 High | 🟡 Medium | 🟢 Low | Tổng |
|--------|---------|-----------|--------|------|
| Cấu hình khung giờ & giá | TC01, TC02, TC04, TC05, TC08 (5) | TC03, TC06, TC07 (3) | TC09 (1) | 9 |
| Đặt sân | TC10, TC11, TC12, TC14, TC19, TC20 (6) | TC13, TC15, TC16, TC18 (4) | TC17 (1) | 11 |
| Thanh toán đặt cọc | TC21, TC22, TC23, TC24, TC25, TC32, TC33, TC34, TC36, TC41 (10) | TC26, TC27, TC29, TC30, TC35, TC37, TC38, TC39, TC42 (9) | TC28, TC31, TC40 (3) | 22 |
| Xác nhận / Từ chối | TC43, TC44, TC46, TC47, TC50 (5) | TC45, TC48, TC49 (3) | — (0) | 8 |
| Check-in | TC51, TC52, TC53 (3) | TC54, TC55 (2) | TC56 (1) | 6 |
| Duyệt sân | TC57, TC58, TC60, TC61 (4) | TC59 (1) | — (0) | 5 |
| Quản lý loại sân | TC64, TC65, TC66, TC67 (4) | TC62, TC63, TC68 (3) | — (0) | 7 |
| **Tổng** | **37** | **25** | **6** | **68** |

| Mức độ | Số TC | Tỉ lệ |
|--------|-------|--------|
| 🔴 High | 37 | 54.4% |
| 🟡 Medium | 25 | 36.8% |
| 🟢 Low | 6 | 8.8% |
| **Tổng** | **68** | **100%** |

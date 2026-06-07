# Tổng hợp kỹ thuật kiểm thử cho các file controller/service Auth, User, Court, SportType

## Các file đã phân tích
- Controller:
  - `backend/src/test/java/group6/it/ou/sportfacilitybooking/controller/AuthControllerTest.java`
  - `backend/src/test/java/group6/it/ou/sportfacilitybooking/controller/UserControllerTest.java`
  - `backend/src/test/java/group6/it/ou/sportfacilitybooking/controller/CourtControllerTest.java`
  - `backend/src/test/java/group6/it/ou/sportfacilitybooking/controller/SportTypeControllerTest.java`
- Service:
  - `backend/src/test/java/group6/it/ou/sportfacilitybooking/service/AuthServiceTest.java`
  - `backend/src/test/java/group6/it/ou/sportfacilitybooking/service/UserServiceTest.java`
  - `backend/src/test/java/group6/it/ou/sportfacilitybooking/service/CourtServiceTest.java`
  - `backend/src/test/java/group6/it/ou/sportfacilitybooking/service/SportTypeServiceTest.java`

## Kỹ thuật kiểm thử được áp dụng
### 1. Equivalence Partitioning (EP)
- Đây là kỹ thuật được sử dụng nhiều nhất.
- Các test thường kiểm tra các lớp tương đương:
  - Request hợp lệ / request không hợp lệ
  - Trường hợp thành công / trường hợp thất bại
  - Dữ liệu tồn tại / không tồn tại
- Ví dụ:
  - `AuthControllerTest` và `AuthServiceTest`: `register` thành công vs email đã tồn tại, `login` thành công vs mật khẩu sai vs tài khoản bị disable.
  - `UserControllerTest` và `UserServiceTest`: tạo user thành công vs exception, lấy profile thành công vs exception.
  - `CourtControllerTest` / `CourtServiceTest`: lấy court, tạo court, cập nhật, xóa với đường thành công và trường hợp exception.
  - `SportTypeControllerTest` / `SportTypeServiceTest`: lấy loại thể thao, tạo loại, cập nhật, với trường hợp success và exception.

### 2. Decision Table (DT)
- Không thấy sử dụng rõ ràng dưới dạng bảng quyết định.
- Tuy nhiên `AuthServiceTest` có các trường hợp nhiều điều kiện khác nhau cho `login`:
  - mật khẩu đúng + user active => thành công
  - mật khẩu sai => thất bại
  - tài khoản disable => thất bại
- Đây có thể xem là một dạng kiểm thử điều kiện kết hợp, nhưng không có bảng quyết định chính thức trong mã.

### 3. Boundary Value Analysis (BVA)
- Hầu như không có test BVA rõ ràng.
- Một vài test dùng giá trị empty string (`SportTypeServiceTest.testCreateSportTypeEmptyName`) là test giá trị không hợp lệ, nhưng không phải BVA theo đúng nghĩa kiểm thử biên độ.
- Không thấy test cho các giá trị biên của số trang, kích thước, id hoặc độ dài chuỗi.

### 4. State Transition (ST)
- Không có mô tả trạng thái chuyển tiếp rõ ràng.
- Có một số test liên quan trạng thái tài khoản người dùng (active / inactive) trong `AuthServiceTest`, nghĩa là hệ thống đang xử lý trạng thái user khác nhau.
- Nhưng các test này chủ yếu là các case tiêu chí đúng/sai hơn là bảng chuyển trạng thái đầy đủ.

## Kết luận chung
- Tổng quan: bộ test này là bộ unit test với hướng tiếp cận positive/negative.
- Kỹ thuật chính: `EP`.
- Kỹ thuật mở rộng: có dấu hiệu `DT` nhẹ trong `AuthServiceTest` vì nhiều điều kiện login.
- Hiện tại không thấy BVA hoặc ST được áp dụng một cách hệ thống.

## Gợi ý
- Nếu cần mở rộng kiểm thử, nên thêm:
  - test BVA cho các trường `page`, `size`, `id`, `password length`, `name length`.
  - test DT cho các hành vi phức tạp như `login+active status+invalid input` và `user update`.
  - test ST nếu có workflow thay đổi trạng thái rõ ràng (active/inactive, locked/unlocked, court status...).

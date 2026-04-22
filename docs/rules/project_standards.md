# Quy chuẩn và Tiêu chuẩn Cấu trúc Dự án Gia Phả

Tài liệu này quy định các tiêu chuẩn bắt buộc áp dụng cho toàn bộ quá trình phát triển hệ thống để đảm bảo tính đồng bộ, hiệu năng và khả năng bảo trì lâu dài.

## 1. Tiêu chuẩn Cơ sở dữ liệu (PostgreSQL)

### Quy tắc đặt tên:
- **Tên bảng & Cột**: Sử dụng `snake_case` (ví dụ: `thanh_vien`, `ngay_sinh`). Tất cả viết thường.
- **Tên bảng**: Để ở dạng số ít (ví dụ: `member` thay vì `members`).
- **Khóa chính**: Luôn đặt tên là `id`, sử dụng kiểu `BIGSERIAL` hoặc `UUID`.

### Thiết kế cấu trúc:
- **Tính toàn vẹn**: Bắt buộc sử dụng Khóa ngoại (Foreign Keys) cho tất cả các mối quan hệ. Không cho phép dư liệu mồ côi.
- **Chỉ mục (Indexing)**: Tạo INDEX cho các cột thường xuyên tìm kiếm (`ho_ten`) và các cột tham chiếu (`parent_id`, `spouse_id`).
- **Xử lý Đệ quy**: Sử dụng **Common Table Expressions (CTE)** để truy vấn cây gia phả nhiều đời nhằm tối ưu hiệu năng so với các vòng lặp code.
- **Ràng buộc**: Sử dụng `CHECK constraints` để đảm bảo dữ liệu hợp lệ (ví dụ: `ngay_mat >= ngay_sinh`).

## 2. Chuẩn mực Backend (Java Spring Boot)

### Cấu trúc Gói (Package Structure):
- `com.giapha.config`: Cấu hình hệ thống (Security, Database, Swagger).
- `com.giapha.controller`: Tiếp nhận và điều hướng các yêu cầu HTTP.
- `com.giapha.service`: Chứa logic nghiệp vụ xử lý dữ liệu.
- `com.giapha.repository`: Giao tiếp trực tiếp với DB (Spring Data JPA).
- `com.giapha.dto`: Các đối tượng vận chuyển dữ liệu (Data Transfer Objects) để bảo mật thông tin Entity.
- `com.giapha.entity`: Ánh xạ cấu trúc bảng DB vào code Java.

### Tiêu chuẩn API:
- **RESTful**: Sử dụng đúng các phương thức `GET`, `POST`, `PUT`, `DELETE`.
- **Phản hồi (Response)**: Luôn trả về cấu trúc đồng bộ: `{ "status": 200, "message": "...", "data": [...] }`.
- **Mã trạng thái**: Sử dụng đúng mã HTTP (200: Thành công, 201: Đã tạo, 400: Lỗi dữ liệu, 401: Chưa đăng nhập, 500: Lỗi server).

## 3. Chuẩn mực Frontend (Angular)

### Cấu trúc Thư mục:
- `app/core`: Chứa các service dùng chung toàn hệ thống (Auth, API Interceptor).
- `app/shared`: Chứa các component, pipe, directive dùng lại nhiều lần.
- `app/features`: Chia nhỏ code theo từng tính năng (FamilyTree, News, Admin).
- `assets/styles`: Chứa các biến màu (Theme Xanh-Cam) và mixins CSS dùng chung.

### Nguyên tắc Phát triển:
- **Component-based**: Chia nhỏ giao diện thành các phần độc lập, dễ tái sử dụng.
- **Reactive Programming**: Ưu tiên sử dụng RxJS để xử lý dữ liệu bất đồng bộ.
- **Responsive**: Giao diện phải tương thích hoàn hảo trên cả Mobile (xem gia phả khi đang đi lễ) và Desktop (quản trị nội dung).

## 4. Chuẩn mực Quy trình & Bảo mật

- **Bảo mật**: Sử dụng JWT để xác thực. Toàn bộ mật khẩu phải được mã hóa bằng `BCrypt`.
- **Ghi chú (Comments)**: Code phải được chú thích rõ ràng bằng tiếng Việt hoặc tiếng Anh về mục đích của hàm/biến.
- **Log**: Ghi lại lịch sử thay đổi dữ liệu quan trọng (ai đã cập nhật thông tin thành viên nào, vào lúc nào).

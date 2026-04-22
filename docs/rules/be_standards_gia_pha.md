# Tiêu chuẩn Backend (Spring Boot) cho Hệ thống Gia Phả

Phần Backend đóng vai trò như "bộ não" xử lý các logic phả hệ phức tạp và cung cấp API chuẩn mực.

## 1. Nghiệp vụ Quản lý Logic Phả Hệ
Dựa trên phân tích các website gia phả (như Gia Phả Số), BE phải xử lý chặt chẽ các trường hợp sau:

- **Logic Sinh Khắc**: API tạo quan hệ gia đình phải Validate độ tuổi hợp lý. (VD: Tuổi cha mẹ phải lớn hơn tuổi con ít nhất 12-15 tuổi).
- **Phân quyền dòng họ (Multi-tenant nhẹ)**: Một phân hệ có thể quản lý nhiều Nhánh/Chi. Phải có cơ chế `branch_id` xác định user có quyền chỉnh sửa thành viên nhánh nào, tránh người nhánh này sửa sai thông tin nhánh khác.
- **Tính toán Đời thứ (Generation Calculation)**: Khi một node (người) được thêm vào làm con của người X (Đời n), hệ thống tự động gán đời của người mới là (n+1).

## 2. Chuẩn mực API cho Phả Đồ (Tree Visualization)
- Front-end thường dùng thư viện như OrgChart JS để vẽ. Các thư viện này yêu cầu định dạng JSON phẳng có chứa `pid` (Parent ID).
- **API `GET /api/v1/family-tree/{branch_id}`**: 
  - Phải trả về mảng dạng phẳng (Flat Array) thay vì Nested Object (đệ quy lồng nhau quá sâu sẽ làm JSON phình to dễ gây lỗi cho các trình duyệt Mobile).
  - Cấu trúc chuẩn: `[ { id: 1, pids: [2], name: "Nguyễn Văn A" }, { id: 2, name: "Nguyễn Thị B" } ]`.
- **Phân trang Phả Đồ (Lazy Loading)**: Với các dòng họ lớn (>1000 người), BE không trả về toàn bộ cây trong 1 API. Phải có tham số `depth` (Số đời tối đa muốn lấy, ví dụ: 3 đời từ người hiện tại) để giảm tải băng thông.

## 3. Module Đặc thù - "Nghĩa trang trực tuyến"
- Hệ thống cần một CronJob/Scheduler (`@Scheduled` trong Spring) chạy mỗi ngày lúc 00:00 để kiểm tra ngày mất (`ngay_mat`) của các thành viên.
- Nếu sắp đến ngày giỗ (cách 3-7 ngày), phải đẩy Notification hoặc Email cho các thành viên trong họ báo về sự kiện này.
- **Xử lý Ngày Âm/Dương**: Các gia phả tại Việt Nam ghi nhận ngày mất chủ yếu là **Ngày Âm lịch**. Backend bắt buộc phải có một hàm tiện ích (Utility) xài thuật toán chuyển đổi thống nhất Âm Dương (Lunisolar Converter) để tính toán chính xác ngày giỗ hàng năm.
- **Port Management**: Luôn kiểm tra và giải phóng cổng (mặc định 8080) sau khi chạy hoặc test server để tránh lỗi "Address already in use".
- **Bảo mật**: Tuyệt đối không commit file `.env` lên Git. Luôn sử dụng placeholders trong `application.yml`.

## 4. Bảo mật API
- **RBAC (Role Based Access Control)**:
  - `ROLE_SUPER_ADMIN`: Toàn quyền website.
  - `ROLE_MODERATOR`: Quản lý nhánh phụ trách.
  - `ROLE_USER`: Chỉ được xem và gửi yêu cầu chỉnh sửa thông tin bản thân/gia đình mình (Cần BQT duyệt).

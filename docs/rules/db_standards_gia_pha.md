# Tiêu chuẩn Cơ sở Dữ liệu (PostgreSQL) cho Hệ thống Gia Phả

Dựa trên việc tham khảo các nền tảng gia phả lớn và chuẩn GEDCOM, đây là các quy tắc và tiêu chuẩn thiết kế DB bắt buộc áp dụng.

## 1. Xử lý Cấu trúc Đệ quy (Phả hệ)
Cây gia phả bản chất là một cấu trúc dữ liệu dạng Đồ thị/Cây phức tạp.
- **Tiêu chuẩn CTE**: Mọi truy vấn lấy danh sách tổ tiên hoặc hậu duệ bắt buộc phải dùng **Recursive CTE (Common Table Expressions)** của PostgreSQL (`WITH RECURSIVE`). Không dùng vòng lặp `for/while` từ Backend để truy vấn từng đời (tránh tình trạng N+1 Query).
- **Tránh Vòng Lặp Vô Hạn**: Phải có trigger hoặc logic kiểm tra ở BE/DB để ngăn chặn việc "người A là cha của người B, và B lại là cha của A".

## 2. Quy chuẩn Bảng Dữ liệu Cốt lõi

### Bảng `thanh_vien` (Member)
- Cột `gioi_tinh`: Lưu bằng INTEGER hoặc ENUM (1: Nam, 2: Nữ, 0: Không xác định). Định dạng theo số để dễ filter và tính toán thứ tự.
- Cột `so_doi` (Generation): Rất quan trọng để hiển thị cây. Cần tự động tính dựa trên sự chênh lệch so với Thủy tổ (Đời 1).
- Cột trạng thái sống/chết (Ví dụ: `is_alive` boolean).

### Bảng `quan_he` (Relationship) - Đa dạng hóa
Gia phả thực tế ở Việt Nam đối mặt với nhiều trường hợp phức tạp, bắt buộc thiết kế CSDL phải đáp ứng được:
- **Đa thê / Đa phu**: Bảng `ket_hon` (Marriage) phải lưu `id_chong`, `id_vo`, `thu_tu_vo` (Vợ cả, Vợ lẽ), `trang_thai` (Đang kết hôn, Đã ly dị).
- **Con nuôi / Con kế**: Bảng `quan_he_con_cai` phải có cột `loai_quan_he` (Con ruột, Con nuôi, Con riêng của vợ/chồng).
- **Chưa rõ thứ tự**: Bảng quan hệ cần cột `thu_tu_sinh` (Con trưởng, Con thứ 2...) để sắp xếp trên phả đồ, thay vì chỉ dựa vào ngày sinh (vì ngày sinh người xưa thường bị thất lạc).

## 3. Quản lý Hiệu năng (Performance)
- **Indexing**: Tạo `B-Tree index` trên các cột `parent_id`, `spouse_id`, `ho_ten`.
- **Soft Delete**: KHÔNG bao giờ dùng lệnh `DELETE` thẳng để xóa thành viên đã có quan hệ chằng chịt. Phải dùng `deleted_at` (Soft Delete). Nếu xóa cứng sẽ làm gãy chuỗi phả đồ mồ côi.

## 4. Tương thích Giao tiếp
- Mọi ID nên sử dụng `UUIDv4` thay vì `Auto Increment ID` nếu có ý định cho phép người dùng import/export file gia phả từ phần mềm khác để tránh xung đột ID.

# MASTER RULE: TIÊU CHUẨN KIẾN TRÚC & NGHIỆP VỤ DỰ ÁN GIA PHẢ

*Văn bản này được soạn thảo bám sát các tiêu chuẩn công nghiệp ngành phần mềm, dưới góc nhìn của System Architect, UI/UX Designer và QA Engineer dạn dày kinh nghiệm, lấy cảm hứng từ luồng dữ liệu của `giaphaonline.vn`.*

---

## 1. Tiêu Chuẩn Giao Diện & Trải Nghiệm (UI/UX - Angular)

Đối tượng người dùng chính là người lớn tuổi và các trưởng họ. Giao diện cần tôn nghiêm, dễ đọc nhưng không kém phần hiện đại.

### 1.1 Phân cấp Thị giác Chữ (Typographic Hierarchy)
Quy định chặtặt chẽ việc sử dụng font chữ nhằm tạo sự tương phản giữa sự trang nghiêm và tính hiện đại:
| Cấp độ | Cấu trúc Typography | Ứng dụng |
|---|---|---|
| **Tiêu đề Lớn (H1, H2)** | **Serif** (VD: *Noto Serif, Playfair Display*) | Tên Dòng họ (Trần Hải Phúc), Tên Thành viên trên Header, Tên Đời thứ. |
| **Tiêu đề Nhỏ (H3, H4)** | **Sans-serif Bold** (VD: *Inter, Roboto*) | Tiêu đề các mục lục, tab chức năng (Tìm kiếm, Phả đồ, Phả ký). |
| **Văn bản Nội dung (P)** | **Sans-serif Regular** (16px - 18px) | Chi tiết tiểu sử, thông báo sư kiện, bài viết kiến thức. |

### 1.2 Phối màu theo quy tắc 60-30-10
Áp dụng chủ điểm "Truyền thống, Tâm linh, Hiện đại":
- **60% (Nền chủ đạo)**: Màu kem sáng (`#F9F6F0`) hoặc be nhạt - tạo cảm giác giấy dó cổ truyền, đỡ mỏi mắt khi đọc phả ký dài.
- **30% (Màu phụ họa)**: Nâu gỗ trầm (`#4A3B32`) hoặc Xanh hoàng gia (`#2C3E50`) - tạo chiều sâu, dùng cho Header, Footer hoa sen và các khối Box.
- **10% (Màu Điểm nhấn - CTA)**: Cam đất (`#D35400`) hoặc Vàng kim mờ (`#D4AF37`) - dùng cho nút Hành động (Thắp hương, Thêm thành viên, Sửa dữ liệu).

### 1.3 Tối ưu hóa Phả Đồ (OrgChart JS & Canvas)
- **Cơ chế Vẽ (Render)**: Bắt buộc áp dụng *Virtualization* (chỉ render các node nằm trong Viewport).
- **Kiểm soát Tương tác (Debounce/Throttle)**: Các sự kiện `mouse-wheel` (zoom) và `drag` phải được bọc qua cờ Throttle (khoảng 16ms, tương đương 60fps) để tránh chuyển động giật lag.
- **Lazy Load Nhánh**: Các nhánh từ Đời thứ 4 trở đi mặc định ở trạng thái `Collapsed`. Click để fetch API lấy tiếp.

---

## 2. Tiêu Chuẩn Backend & Database (Spring Boot + PostgreSQL)

Hệ thống gia phả ẩn chứa những nghiệp vụ cực kì phức tạp khi quy mô lên tới hàng nghìn người trải dài qua nhiều thế kỷ.

### 2.1 Xử lý Edge-cases Nghiệp vụ Cốt lõi
Phải xử lý triệt để mức Database và Business Logic:

| Trường hợp (Edge-case) | Cách xử lý (Database & Code) |
|---|---|
| **Con nuôi / Con riêng** | Bảng `relationships` sử dụng cột `loai_quan_he` (ENUM: `RUOT`, `NUOI`, `RIENG`). Cây phả đồ sẽ dùng nét đứt để thể hiện quan hệ nuôi/riêng. |
| **Đa thê / Đa phu** | Bảng `marriages` dùng cột `thu_tu_xep_hang` (1: Vợ cả, 2: Vợ hai). Logic CTE nhóm các vợ/chồng theo 1 cụm Node trước khi nối tới con cái. |
| **Không rõ sinh/mất** | Kiểu dữ liệu linh hoạt. Bảng `members` bổ sung `nam_sinh_du_doan`. Nếu Null, dùng thuật toán Approximation (Tuổi cha = Tuổi con + 25) để sắp thứ tự con. |

### 2.2 Tối ưu hóa Recursive CTE trong PostgreSQL
Khi dòng họ > 5000 người, đệ quy thông thường sẽ bị thắt cổ chai.
- **Kỹ thuật LTREE (Đề xuất mạnh)**: Cài `extension ltree`. Cột `path` lưu cấu trúc `1.4.12` nhằm truy vấn hậu duệ siêu tốc qua Index `path <@ '1.4'`.
- **Giới hạn Độ sâu (Depth Limit)**: Câu lệnh CTE bắt buộc có điều kiện `depth < X` loại bỏ vòng lặp vô tận.
- **Composite Indexing**: Tạo Index gộp: `CREATE INDEX idx_parent_child ON relationships(parent_id, child_id) INCLUDE (loai_quan_he);` để tăng tốc join bảng.

---

## 3. Tiêu Chuẩn Kiểm Thử & QA (QA Engineering)

Chất lượng dữ liệu Gia Phả là bất khả xâm phạm. Hệ thống phải vượt qua các bài kiểm định gắt gao.

### 3.1 Phân tích Giá trị biên (Boundary Value Analysis - BVA)
Đội cận QA (BE + QA) phải bắt Test Case dựa trên BVA:
- **Biên Ngày Tháng**: Ngày mất KHÔNG ĐƯỢC nhỏ hơn Ngày sinh (`death_date >= birth_date`).
- **Biên Tuổi Quan Hệ**:
  - Khoảng cách tuổi Cha/Mẹ và Con Ruột: Biên dưới `min = 12`, biên trên `max = 70` tuổi. Nằm ngoài biên này, hệ thống cảnh báo (Warning) thay vì Block (do sai số lịch sử), nhưng buộc phải có Confirm.
- **Biên Đời Thứ (Generation)**: BVA từ `1` (Thủy tổ) đến `99` (Ngưỡng cảnh báo đứt gãy phả hệ).

### 3.2 Kịch bản Kiểm thử Hộp đen (Black-box) - Luồng Phân quyền
Kiểm tra khả năng bảo mật theo ma trận RBAC:
- **Test Case 1**: Tài khoản Viewer (`ROLE_VIEWER`) không có nút Edit. Nếu dùng cURL chọc thẳng API `PUT /members`, server phải ném `403 Forbidden`.
- **Test Case 2**: Trưởng chi nhánh A (`ROLE_MODERATOR(A)`) chỉnh sửa thành viên nhánh B. Hệ thống phải chặn và quăng Exception `"Unauthorized Branch Operation"`.
- **Test Case 3**: Tính năng Nghĩa trang ảo phải chịu tải (JMeter Load Test) 500 CCU vào ngày thanh minh, không bị lỗi Race Condition khi update số lượng thắp hương.

### 3.3 Kịch bản Kiểm thử Hộp trắng (White-box) - Thuật toán
Kiểm tra sức bền kiến trúc tầng Service:
- **Path Coverage**: Viết Unit Test (JUnit + Mockito) mô phỏng nhánh tuần hoàn (A là cha B, B nhầm là cha A). Thuật toán phải văng `CircularDependencyException` ngay, tránh `StackOverflow`.
- **Performance Assert**: Mock `10,000` records để chạy Performance Test hàm Map Json ra Flat Array cho OrgChart JS. Phải hoàn thành dưới `< 500ms`.

---

## 4. Tiêu Chuẩn Trợ Lý AI (AI Assistant Rules)

- **Xác nhận hoàn thành**: Mỗi lần đọc doc và thực hiện code thì phải có dòng `đã hoàn thành công việc` ở cuối câu trả lời để người dùng có thể nhận biết được rằng AI đang bám sát và tuân theo rule đã đề ra.

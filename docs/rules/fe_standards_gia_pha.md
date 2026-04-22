# Tiêu chuẩn Frontend (Angular) cho Cây Gia Phả

Frontend là nơi tương tác trực tiếp với người lớn tuổi trong họ, do đó trải nghiệm người dùng (UX/UI) và khả năng tiếp cận (Accessibility) là ưu tiên hàng đầu.

## 1. Thiết kế Giao diện (UI/UX)
Theo yêu cầu thiết kế (Chủ đề Hoa Sen - Trang nghiêm, Hiện đại):
- **Bố cục (Layout)**: Rộng rãi, chữ to, rõ ràng. Người dùng gia phả thường là các cụ già. 
- **Font chữ**: Sử dụng font có chân (Serif) cho các tiêu đề lớn như tên họ, tên đời (tạo cảm giác cổ kính, truyền thống). Phần nội dung chi tiết dùng font không chân (Sans-serif) để dễ đọc trên màn hình điện thoại.
- **Micro-interactions (Hiệu ứng)**: Khi di chuột (hover) vào hình hoa sen ở footer/header thì hoa nở nhẹ hoặc sáng lên tĩnh lặng, không sử dụng hiệu ứng giật gân, nhấp nháy phức tạp.

## 2. Tiêu chuẩn Phả Đồ Tương Tác (OrgChart Component)
Khu vực vẽ Phả đô là phần cốt lõi và phức tạp nhất ở Frontend:
- **Nguyên tắc Hiển thị**: Không bao giờ vẽ TOÀN BỘ gia phả lên màn hình ngay từ đầu nếu dòng họ lớn (>500 người). Mặc định chỉ hiển thị cụ Tổ và 2-3 đời tiếp theo. Các nhánh khác được thu gọn (Collapsed).
- **Phân loại nhánh (Branching)**: Các thành viên nam hiển thị hộp (box) vuông vắn, nữ là hộp bo tròn hoặc đánh dấu phân biệt bằng viền, nhằm theo dõi truyền thống phân nhánh.
- **Responsive Panning & Zooming**: Cây gia phả phải hỗ trợ kéo thả (drag & drop) màn hình và phóng to/thu nhỏ (Pin-to-zoom trên điện thoại, Scroll trên PC).
- **Xuất file PDF lớn**: Phải tích hợp chức năng chụp lại Canvas của phả đồ ra file PDF khổ lớn (A0, A1) để người dùng có thể mang đi in và treo ở Nhà Thờ Họ.

## 3. Kiến trúc Component Angular
- **Component Nguyên Tử (Dumb Components)**: `MemberCard`, `LotusDivider`, `TreeBox`. Các component này chỉ nhận dữ liệu `@Input()` và hiển thị, không gọi API.
- **Component Trạng thái (Smart Components)**: `TreeViewContainer`, `MemorialBoard`. Nơi đây kết nối trực tiếp với Services, dùng NgRx hoặc RxJS BehaviorSubject để lấy dữ liệu.
- **Tối ưu hiển thị**: Nếu danh sách dòng họ quá dài trong "Phả Ký" (Danh sách lý lịch), bắt buộc dùng tính năng `Virtual Scrolling` (`@angular/cdk/scrolling`) để tránh làm đơ DOM của trình duyệt.

## 4. Các Chức năng Tích hợp Ưu Tiên
- **Tìm kiếm Real-time**: Khi đánh 1 cái tên vào khung Search, sơ đồ sẽ tự động Highlight ô của người đó, đồng thời tự động pan (di chuyển khung hình) đến vị trí người đó trên cây.
- **Chạm biểu tượng (Iconography)**: Trong box thành viên đã mất, tự động hiện biểu tượng nén nhang. Khi click vào sẽ mở Modal "Nghĩa trang trực tuyến" để gửi lời tưởng niệm. 

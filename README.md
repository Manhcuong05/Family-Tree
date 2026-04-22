# Family Tree - Gia Phả Dòng Họ

Dự án quản lý Gia Phả Dòng Họ, xây dựng trên nền tảng:
- Backend: Spring Boot 3, Java 17, PostgreSQL
- Frontend: Angular (sẽ triển khai)

## Tính năng (Backend)
- Quản lý thành viên gia phả (Recursive CTE cho hiệu suất cao với hàng ngàn Node).
- Phân quyền (SUPER_ADMIN, MODERATOR, USER) sử dụng Spring Security và JWT.
- Hệ thống chặn vòng lặp vô tận (Circular Dependency Validation).
- Quản lý đa thê, con nuôi, con riêng.
- CronJob nhắc nhở ngày giỗ tự động bằng lịch Âm/Dương.

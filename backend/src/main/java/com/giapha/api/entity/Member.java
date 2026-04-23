package com.giapha.api.entity;

import com.giapha.api.enums.Gender;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "members")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE members SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "branch_id")
    private UUID branchId;

    @Column(name = "ho_ten", nullable = false, length = 100)
    private String hoTen;

    @Column(name = "ten_goi_khac", length = 100)
    private String tenGoiKhac;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "gioi_tinh", nullable = false)
    private Gender gioiTinh;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "ngay_sinh")
    private LocalDate ngaySinh;

    @Column(name = "ngay_sinh_am_lich", length = 50)
    private String ngaySinhAmLich;

    @Column(name = "nam_sinh_du_doan")
    private Integer namSinhDuDoan;

    @Column(name = "is_alive", nullable = false)
    @Builder.Default
    private Boolean isAlive = true;

    // Đảm bảo Jackson không ghi đè null vào trường này
    public void setIsAlive(Boolean isAlive) {
        this.isAlive = isAlive != null ? isAlive : true;
    }

    public Boolean getIsAlive() {
        return isAlive != null ? isAlive : true;
    }

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "ngay_mat")
    private LocalDate ngayMat;

    @Column(name = "ngay_mat_am_lich", length = 50)
    private String ngayMatAmLich;

    @Column(name = "ngay_gio", length = 50)
    private String ngayGio;

    @Column(name = "so_doi", nullable = false)
    private Integer soDoi;

    @Column(name = "tieu_su", columnDefinition = "TEXT")
    private String tieuSu;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @PrePersist
    protected void onCreate() {
        if (isAlive == null) {
            isAlive = true;
        }
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

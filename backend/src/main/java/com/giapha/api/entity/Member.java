package com.giapha.api.entity;

import com.giapha.api.enums.Gender;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

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

    @Column(name = "branch_id", nullable = false)
    private UUID branchId;

    @Column(name = "ho_ten", nullable = false, length = 100)
    private String hoTen;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "gioi_tinh", nullable = false)
    private Gender gioiTinh;

    @Column(name = "ngay_sinh")
    private LocalDate ngaySinh;

    @Column(name = "nam_sinh_du_doan")
    private Integer namSinhDuDoan;

    @Column(name = "is_alive", nullable = false)
    @Builder.Default
    private Boolean isAlive = true;

    @Column(name = "ngay_mat")
    private LocalDate ngayMat;

    @Column(name = "so_doi", nullable = false)
    private Integer soDoi;

    @Column(name = "tieu_su", columnDefinition = "TEXT")
    private String tieuSu;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

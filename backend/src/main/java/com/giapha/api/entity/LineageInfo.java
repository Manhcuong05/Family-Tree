package com.giapha.api.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "lineage_info")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LineageInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "branch_id")
    private UUID branchId;

    @Column(name = "ten_dong_ho", nullable = false)
    private String tenDongHo;

    @Column(name = "loi_ngo", columnDefinition = "TEXT")
    private String loiNgo;

    @Column(name = "lich_su", columnDefinition = "TEXT")
    private String lichSu;

    @Column(name = "dia_chi_nha_tho")
    private String diaChiNhaTho;

    @Column(name = "banner_url")
    private String bannerUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

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

package com.giapha.api.entity;

import com.giapha.api.enums.MarriageStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "marriages", indexes = {
    @Index(name = "idx_husband_wife", columnList = "husband_id, wife_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Marriage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "husband_id", nullable = false)
    private Member husband;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wife_id", nullable = false)
    private Member wife;

    @Column(name = "thu_tu_vo")
    private Integer thuTuVo; // 1: Vợ cả, 2: Vợ hai

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false)
    @Builder.Default
    private MarriageStatus trangThai = MarriageStatus.DANG_KET_HON;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

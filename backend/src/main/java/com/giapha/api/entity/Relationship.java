package com.giapha.api.entity;

import com.giapha.api.enums.RelationshipType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "relationships", indexes = {
    @Index(name = "idx_parent_child", columnList = "parent_id, child_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Relationship {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", nullable = false)
    private Member parent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id", nullable = false)
    private Member child;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_quan_he", nullable = false)
    @Builder.Default
    private RelationshipType loaiQuanHe = RelationshipType.RUOT;

    @Column(name = "thu_tu_sinh")
    private Integer thuTuSinh;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

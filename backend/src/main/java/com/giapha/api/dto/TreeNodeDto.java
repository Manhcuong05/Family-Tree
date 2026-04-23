package com.giapha.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TreeNodeDto {
    // === Thông tin bản thân ===
    private String id;
    private String hoTen;
    private String avatarUrl;
    private String gioiTinh;
    private Integer soDoi;
    private Boolean isAlive;
    private String ngaySinhAmLich;
    private String ngayMatAmLich;
    private String tieuSu;

    // === Quan hệ với cha ===
    private String parentId;
    private String loaiQuanHe; // RUOT | NUOI | RIENG | DAU_RE

    // === Thông tin vợ/chồng (nếu có) ===
    private String spouseId;
    private String spouseHoTen;
    private String spouseAvatarUrl;
    private String spouseGioiTinh;
    private Boolean spouseIsAlive;
    private Integer thuTuVo;          // 1 = vợ cả, 2 = vợ hai...
    private String marriageStatus;    // DANG_KET_HON | LY_HON | DA_MAT
}

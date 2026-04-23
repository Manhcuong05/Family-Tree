package com.giapha.api.dto;

import com.giapha.api.entity.Member;
import com.giapha.api.enums.Gender;
import com.giapha.api.enums.RelationshipType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberListItemDto {
    private UUID id;
    private String hoTen;
    private String avatarUrl;
    private Gender gioiTinh;
    private Integer soDoi;
    private Boolean isAlive;
    private String ngaySinhAmLich;
    private String ngayMatAmLich;
    private String tieuSu;
    private UUID parentId;
    private RelationshipType loaiQuanHe;
}

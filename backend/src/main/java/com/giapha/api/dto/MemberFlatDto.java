package com.giapha.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberFlatDto {
    private String id;
    private String parentId; 
    private String name;
    private Integer soDoi;
    private String avatarUrl;
}

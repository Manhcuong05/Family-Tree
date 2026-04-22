package com.giapha.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberFlatDto {
    private String id;
    private String pid; // Parent ID
    private String name;
    private Integer soDoi;
}

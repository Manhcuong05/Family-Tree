package com.giapha.api.dto;

import com.giapha.api.entity.Member;
import com.giapha.api.enums.RelationshipType;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateMemberRequest {
    private Member member;
    private UUID parentId;
    private RelationshipType relationshipType;
}

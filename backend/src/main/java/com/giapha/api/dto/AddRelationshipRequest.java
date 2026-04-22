package com.giapha.api.dto;

import com.giapha.api.enums.RelationshipType;
import lombok.Data;

import java.util.UUID;

@Data
public class AddRelationshipRequest {
    private UUID parentId;
    private UUID childId;
    private RelationshipType type;
}

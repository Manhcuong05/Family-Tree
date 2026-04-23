package com.giapha.api.repository;

import com.giapha.api.entity.Relationship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RelationshipRepository extends JpaRepository<Relationship, UUID> {
    List<Relationship> findByParentId(UUID parentId);
    List<Relationship> findByChildId(UUID childId);

    boolean existsByParentId(UUID parentId);
    boolean existsByChildId(UUID childId);
    void deleteByChildId(UUID childId);
    boolean existsByParentIdAndChildId(UUID parentId, UUID childId);
}

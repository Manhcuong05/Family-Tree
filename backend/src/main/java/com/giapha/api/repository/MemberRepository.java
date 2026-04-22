package com.giapha.api.repository;

import com.giapha.api.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MemberRepository extends JpaRepository<Member, UUID> {

    List<Member> findByBranchId(UUID branchId);

    // Native query using Recursive CTE to get descendants of a root member
    // Note: To return a custom flat structure (like Map or DTO) instead of Member entity, 
    // we often use an interface-based projection or a DTO.
    @Query(value = """
        WITH RECURSIVE descendants AS (
            SELECT m.id, m.ho_ten as hoTen, m.so_doi as soDoi, r.parent_id as parentId
            FROM members m
            LEFT JOIN relationships r ON m.id = r.child_id
            WHERE m.id = :rootId
            
            UNION
            
            SELECT m.id, m.ho_ten, m.so_doi, r.parent_id
            FROM members m
            INNER JOIN relationships r ON m.id = r.child_id
            INNER JOIN descendants d ON r.parent_id = d.id
        )
        SELECT id, hoTen, soDoi, parentId FROM descendants
    """, nativeQuery = true)
    List<Object[]> findDescendantsFlat(@Param("rootId") UUID rootId);
}

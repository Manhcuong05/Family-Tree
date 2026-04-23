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
            SELECT m.id, m.ho_ten as hoTen, m.so_doi as soDoi, 
                   r.parent_id as parentId, m.avatar_url as avatarUrl,
                   m.gioi_tinh as gioiTinh, m.is_alive as isAlive,
                   m.ngay_sinh_am_lich as ngaySinhAmLich,
                   m.ngay_mat_am_lich as ngayMatAmLich,
                   m.tieu_su as tieuSu,
                   r.loai_quan_he as loaiQuanHe
            FROM members m
            LEFT JOIN relationships r ON m.id = r.child_id AND r.loai_quan_he != 'VO_CHONG'
            WHERE m.id = :rootId AND m.deleted_at IS NULL
            
            UNION
            
            SELECT m.id, m.ho_ten, m.so_doi, r.parent_id, m.avatar_url,
                   m.gioi_tinh, m.is_alive,
                   m.ngay_sinh_am_lich, m.ngay_mat_am_lich, m.tieu_su,
                   r.loai_quan_he
            FROM members m
            INNER JOIN relationships r ON m.id = r.child_id AND r.loai_quan_he != 'VO_CHONG'
            INNER JOIN descendants d ON r.parent_id = d.id
            WHERE m.deleted_at IS NULL
        )
        SELECT id, hoTen, soDoi, parentId, avatarUrl, gioiTinh, isAlive,
               ngaySinhAmLich, ngayMatAmLich, tieuSu, loaiQuanHe FROM descendants
    """, nativeQuery = true)
    List<Object[]> findDescendantsFlat(@Param("rootId") UUID rootId);
}

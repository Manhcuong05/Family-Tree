package com.giapha.api.repository;

import com.giapha.api.entity.Marriage;
import com.giapha.api.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MarriageRepository extends JpaRepository<Marriage, UUID> {

    // Lấy tất cả hôn nhân có liên quan đến 1 tập danh sách thành viên
    @Query("SELECT m FROM Marriage m WHERE m.husband.id IN :ids OR m.wife.id IN :ids")
    List<Marriage> findByMemberIds(@Param("ids") List<UUID> ids);

    List<Marriage> findByHusband(Member husband);
    List<Marriage> findByWife(Member wife);
}

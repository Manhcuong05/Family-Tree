package com.giapha.api.repository;

import com.giapha.api.entity.LineageInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface LineageInfoRepository extends JpaRepository<LineageInfo, UUID> {
    Optional<LineageInfo> findByBranchId(UUID branchId);
}

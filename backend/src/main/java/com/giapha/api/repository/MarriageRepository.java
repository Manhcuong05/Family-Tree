package com.giapha.api.repository;

import com.giapha.api.entity.Marriage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MarriageRepository extends JpaRepository<Marriage, UUID> {
    List<Marriage> findByHusbandId(UUID husbandId);
    List<Marriage> findByWifeId(UUID wifeId);
}

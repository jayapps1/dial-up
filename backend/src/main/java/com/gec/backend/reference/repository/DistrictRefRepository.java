package com.gec.backend.reference.repository;

import com.gec.backend.reference.entity.DistrictRefEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface DistrictRefRepository extends JpaRepository<DistrictRefEntity, UUID> {
    List<DistrictRefEntity> findByRegionIdAndActiveTrueOrderByNameAsc(UUID regionId);
    List<DistrictRefEntity> findByRegionIdAndNameContainingIgnoreCaseAndActiveTrueOrderByNameAsc(UUID regionId, String search);
}

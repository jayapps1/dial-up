package com.gec.backend.reference.repository;

import com.gec.backend.reference.entity.RegionRefEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface RegionRefRepository extends JpaRepository<RegionRefEntity, UUID> {
    List<RegionRefEntity> findByActiveTrueOrderByNameAsc();
}

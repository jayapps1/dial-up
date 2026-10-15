package com.gec.backend.agency.repository;

import com.gec.backend.agency.entity.AgencyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AgencyRepository extends JpaRepository<AgencyEntity, UUID> {
    List<AgencyEntity> findByActiveTrueAndVerifiedTrue();
    Optional<AgencyEntity> findByIdAndActiveTrueAndVerifiedTrue(UUID id);
}

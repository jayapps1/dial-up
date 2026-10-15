package com.gec.backend.admin.repository;

import com.gec.backend.admin.entity.AdminAccessRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface AdminAccessRequestRepository extends JpaRepository<AdminAccessRequestEntity, UUID> {
    List<AdminAccessRequestEntity> findByStatusOrderByCreatedAtDesc(String status);
    List<AdminAccessRequestEntity> findByAgencyIdOrderByCreatedAtDesc(UUID agencyId);
    boolean existsByCandidateEmailIgnoreCaseAndStatus(String candidateEmail, String status);
}

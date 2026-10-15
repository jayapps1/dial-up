package com.gec.backend.report.repository;

import com.gec.backend.report.entity.ReportStatusHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ReportStatusHistoryRepository extends JpaRepository<ReportStatusHistoryEntity, UUID> {
    List<ReportStatusHistoryEntity> findByReportIdOrderByCreatedAtAsc(UUID reportId);
}

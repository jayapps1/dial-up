package com.gec.backend.report.dto;

import com.gec.backend.agency.entity.AgencyType;
import com.gec.backend.report.entity.ReportStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public record TrackedReportResponse(
    String publicReference,
    String categoryName,
    ReportStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    Set<AgencyType> targetAgencyTypes,
    List<ReportStatusHistoryDto> statusHistory
) {
    public record ReportStatusHistoryDto(
        ReportStatus status,
        String note,
        LocalDateTime timestamp
    ) {}
}

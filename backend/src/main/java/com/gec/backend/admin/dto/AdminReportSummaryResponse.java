package com.gec.backend.admin.dto;

import com.gec.backend.agency.entity.AgencyType;
import com.gec.backend.report.entity.IncidentSeverity;
import com.gec.backend.report.entity.ReportMode;
import com.gec.backend.report.entity.ReportStatus;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record AdminReportSummaryResponse(
    UUID id,
    String publicReference,
    UUID categoryId,
    String categoryName,
    ReportMode mode,
    IncidentSeverity severity,
    ReportStatus status,
    LocalDateTime createdAt,
    Set<AgencyType> targetAgencyTypes
) {}

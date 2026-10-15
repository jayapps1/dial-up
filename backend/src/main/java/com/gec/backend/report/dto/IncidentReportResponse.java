package com.gec.backend.report.dto;

import com.gec.backend.agency.entity.AgencyType;
import com.gec.backend.report.entity.IncidentSeverity;
import com.gec.backend.report.entity.ReportMode;
import com.gec.backend.report.entity.ReportStatus;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record IncidentReportResponse(
    UUID id,
    String publicReference,
    String trackingToken,
    ReportStatus status,
    ReportMode mode,
    IncidentSeverity severity,
    LocalDateTime createdAt,
    Set<AgencyType> targetAgencyTypes
) {}

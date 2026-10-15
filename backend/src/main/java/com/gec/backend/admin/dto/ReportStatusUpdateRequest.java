package com.gec.backend.admin.dto;

import com.gec.backend.report.entity.ReportStatus;
import jakarta.validation.constraints.NotNull;

public record ReportStatusUpdateRequest(
    @NotNull ReportStatus status,
    String note
) {}

package com.gec.backend.admin.dto;

import com.gec.backend.agency.entity.AgencyType;
import jakarta.validation.constraints.NotNull;

public record ReportTransferRequest(
    @NotNull AgencyType targetAgencyType,
    String note
) {}

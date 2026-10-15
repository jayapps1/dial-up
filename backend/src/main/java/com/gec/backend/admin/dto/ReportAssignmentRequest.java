package com.gec.backend.admin.dto;

import java.util.UUID;

public record ReportAssignmentRequest(
    UUID stationId,
    String note
) {}

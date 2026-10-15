package com.gec.backend.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

public record CreateUserManagementRequest(
    @NotBlank String requestType,
    @NotNull UUID targetUserId,
    @NotBlank String reason,
    LocalDate effectiveDate
) {}

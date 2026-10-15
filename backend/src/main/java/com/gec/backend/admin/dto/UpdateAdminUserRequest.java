package com.gec.backend.admin.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.Set;
import java.util.UUID;

public record UpdateAdminUserRequest(
    @NotBlank String firstName,
    @NotBlank String lastName,
    String phoneNumber,
    UUID agencyId,
    String region,
    UUID stationId,
    Set<String> roleCodes
) {}

package com.gec.backend.admin.dto;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record TemporaryCredentialResponse(
    UUID userId,
    String firstName,
    String lastName,
    String email,
    Set<String> roles,
    UUID agencyId,
    String agencyName,
    String region,
    UUID stationId,
    String stationName,
    boolean mustChangePassword,
    LocalDateTime temporaryPasswordIssuedAt,
    LocalDateTime temporaryPasswordExpiresAt,
    LocalDateTime lastLoginAt
) {}

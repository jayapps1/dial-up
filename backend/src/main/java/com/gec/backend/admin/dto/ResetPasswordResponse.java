package com.gec.backend.admin.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ResetPasswordResponse(
    UUID userId,
    String email,
    String temporaryPassword,
    boolean mustChangePassword,
    LocalDateTime issuedAt,
    LocalDateTime expiresAt
) {}

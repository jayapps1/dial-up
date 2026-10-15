package com.gec.backend.admin.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record AdminAccessRequestResponse(
    UUID id,
    String requestType,
    UUID agencyId,
    String requestedRole,
    String candidateFirstName,
    String candidateLastName,
    String candidateEmail,
    String candidatePhone,
    String reason,
    String status,
    UUID requestedBy,
    UUID reviewedBy,
    LocalDateTime reviewedAt,
    String rejectionReason,
    LocalDateTime createdAt
) {}

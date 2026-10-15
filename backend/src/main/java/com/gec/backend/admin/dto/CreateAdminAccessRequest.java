package com.gec.backend.admin.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateAdminAccessRequest(
    @NotBlank String candidateFirstName,
    @NotBlank String candidateLastName,
    @NotBlank String candidateEmail,
    String candidatePhone,
    @NotBlank String reason
) {}

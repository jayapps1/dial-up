package com.gec.backend.admin.dto;

import com.gec.backend.agency.entity.AgencyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateAgencyRequest(
    @NotBlank String name,
    @NotBlank String code,
    @NotNull AgencyType type,
    String emergencyNumber
) {}

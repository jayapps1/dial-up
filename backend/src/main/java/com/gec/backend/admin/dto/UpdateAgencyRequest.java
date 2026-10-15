package com.gec.backend.admin.dto;

import com.gec.backend.agency.entity.AgencyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateAgencyRequest(
    @NotBlank String name,
    @NotNull AgencyType type,
    String emergencyNumber
) {}

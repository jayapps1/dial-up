package com.gec.backend.report.dto;

import com.gec.backend.report.entity.IncidentSeverity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateCategoryRequest(
    @NotBlank String name,
    String description,
    @NotNull IncidentSeverity defaultSeverity,
    boolean supportsSilent,
    boolean supportsGeneral,
    boolean supportsCall
) {}

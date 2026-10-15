package com.gec.backend.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateStationRequest(
    @NotBlank String name,
    @NotBlank String region,
    @NotBlank String district,
    String locality,
    @NotNull Double latitude,
    @NotNull Double longitude,
    Double locationAccuracyMeters
) {}

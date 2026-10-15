package com.gec.backend.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateStationRequest(
    @NotNull UUID agencyId,
    @NotBlank String name,
    @NotBlank String code,
    @NotBlank String region,
    @NotBlank String district,
    String locality,
    @NotNull Double latitude,
    @NotNull Double longitude,
    Double locationAccuracyMeters
) {}

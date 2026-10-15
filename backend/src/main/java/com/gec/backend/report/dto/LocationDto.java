package com.gec.backend.report.dto;

public record LocationDto(
    Double latitude,
    Double longitude,
    Double accuracyMeters,
    String landmark,
    String locality,
    String district,
    String region
) {}

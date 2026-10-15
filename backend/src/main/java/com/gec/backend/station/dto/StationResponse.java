package com.gec.backend.station.dto;

import com.gec.backend.reference.entity.StationLevel;
import java.util.UUID;

public record StationResponse(
    UUID id,
    UUID agencyId,
    String name,
    String code,
    StationLevel stationLevel,
    String region,
    String district,
    UUID regionId,
    UUID districtId,
    String locality,
    Double latitude,
    Double longitude,
    Double locationAccuracyMeters,
    boolean active,
    boolean verified
) {}

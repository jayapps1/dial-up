package com.gec.backend.station.dto;

import com.gec.backend.station.entity.StationContactType;
import java.util.UUID;

public record StationContactResponse(
    UUID id,
    UUID stationId,
    String phoneNumber,
    StationContactType type,
    int priority,
    boolean is24Hour,
    boolean active,
    boolean verified
) {}

package com.gec.backend.agency.dto;

import com.gec.backend.agency.entity.AgencyType;
import java.util.UUID;

public record AgencyResponse(
    UUID id,
    String name,
    String code,
    AgencyType type,
    String emergencyNumber,
    boolean active,
    boolean verified
) {}

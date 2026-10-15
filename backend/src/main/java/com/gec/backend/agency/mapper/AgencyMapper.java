package com.gec.backend.agency.mapper;

import com.gec.backend.agency.dto.AgencyResponse;
import com.gec.backend.agency.entity.AgencyEntity;
import org.springframework.stereotype.Component;

@Component
public class AgencyMapper {

    public AgencyResponse toResponse(AgencyEntity entity) {
        if (entity == null) return null;
        return new AgencyResponse(
            entity.getId(),
            entity.getName(),
            entity.getCode(),
            entity.getType(),
            entity.getEmergencyNumber(),
            entity.isActive(),
            entity.isVerified()
        );
    }
}

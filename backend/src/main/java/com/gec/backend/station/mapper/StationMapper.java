package com.gec.backend.station.mapper;

import com.gec.backend.station.dto.StationContactResponse;
import com.gec.backend.station.dto.StationResponse;
import com.gec.backend.station.entity.StationContactEntity;
import com.gec.backend.station.entity.StationEntity;
import org.springframework.stereotype.Component;

@Component
public class StationMapper {

    public StationResponse toResponse(StationEntity entity) {
        if (entity == null) return null;
        return new StationResponse(
            entity.getId(),
            entity.getAgencyId(),
            entity.getName(),
            entity.getCode(),
            entity.getStationLevel(),
            entity.getRegion(),
            entity.getDistrict(),
            entity.getRegionId(),
            entity.getDistrictId(),
            entity.getLocality(),
            entity.getLatitude(),
            entity.getLongitude(),
            entity.getLocationAccuracyMeters(),
            entity.isActive(),
            entity.isVerified()
        );
    }

    public StationContactResponse toContactResponse(StationContactEntity entity) {
        if (entity == null) return null;
        return new StationContactResponse(
            entity.getId(),
            entity.getStationId(),
            entity.getPhoneNumber(),
            entity.getContactType(),
            entity.getPriority(),
            entity.isAvailable24Hour(),
            entity.isActive(),
            entity.isVerified()
        );
    }
}

package com.gec.backend.admin.dto;

import com.gec.backend.station.entity.StationContactType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateStationContactRequest(
    @NotBlank String phoneNumber,
    @NotNull StationContactType type,
    int priority,
    boolean publicContact,
    boolean available24Hour
) {}

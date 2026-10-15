package com.gec.backend.station.dto;

import java.util.List;

public record StationDetailsResponse(
    StationResponse station,
    List<StationContactResponse> contacts
) {}

package com.gec.backend.report.dto;

public record VehicleInfoDto(
    String registrationNumber,
    String vehicleType,
    String colour,
    String directionOfTravel
) {}

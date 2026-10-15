package com.gec.backend.agency.controller;

import com.gec.backend.agency.dto.AgencyResponse;
import com.gec.backend.agency.service.AgencyService;
import com.gec.backend.station.dto.StationResponse;
import com.gec.backend.station.service.StationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/public/agencies")
public class PublicAgencyController {

    private final AgencyService agencyService;
    private final StationService stationService;

    public PublicAgencyController(AgencyService agencyService, StationService stationService) {
        this.agencyService = agencyService;
        this.stationService = stationService;
    }

    @GetMapping
    public ResponseEntity<List<AgencyResponse>> getActiveVerifiedAgencies() {
        return ResponseEntity.ok(agencyService.getActiveVerifiedAgencies());
    }

    @GetMapping("/{agencyId}")
    public ResponseEntity<AgencyResponse> getAgencyById(@PathVariable UUID agencyId) {
        return ResponseEntity.ok(agencyService.getAgencyById(agencyId));
    }

    @GetMapping("/{agencyId}/stations")
    public ResponseEntity<List<StationResponse>> getStationsByAgency(@PathVariable UUID agencyId) {
        return ResponseEntity.ok(stationService.getActiveVerifiedStationsByAgency(agencyId));
    }
}

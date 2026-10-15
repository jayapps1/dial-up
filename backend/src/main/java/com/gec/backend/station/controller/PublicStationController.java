package com.gec.backend.station.controller;

import com.gec.backend.station.dto.StationContactResponse;
import com.gec.backend.station.dto.StationDetailsResponse;
import com.gec.backend.station.service.StationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/public/stations")
public class PublicStationController {

    private final StationService stationService;

    public PublicStationController(StationService stationService) {
        this.stationService = stationService;
    }

    @GetMapping("/{stationId}")
    public ResponseEntity<StationDetailsResponse> getStationDetails(@PathVariable UUID stationId) {
        return ResponseEntity.ok(stationService.getStationDetails(stationId));
    }

    @GetMapping("/{stationId}/contacts")
    public ResponseEntity<List<StationContactResponse>> getStationContacts(@PathVariable UUID stationId) {
        return ResponseEntity.ok(stationService.getStationContacts(stationId));
    }
}

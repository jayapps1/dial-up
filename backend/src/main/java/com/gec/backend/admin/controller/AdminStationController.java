package com.gec.backend.admin.controller;

import com.gec.backend.admin.dto.*;
import com.gec.backend.station.dto.StationContactResponse;
import com.gec.backend.station.dto.StationDetailsResponse;
import com.gec.backend.station.dto.StationResponse;
import com.gec.backend.station.service.StationService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/stations")
public class AdminStationController {

    private final StationService stationService;

    public AdminStationController(StationService stationService) {
        this.stationService = stationService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('STATION_READ')")
    public ResponseEntity<Page<StationResponse>> getAdminStations(Pageable pageable) {
        return ResponseEntity.ok(stationService.getAdminStations(pageable));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAuthority('STATION_READ')")
    public ResponseEntity<List<StationResponse>> searchStations(@RequestParam(required = false) String q) {
        return ResponseEntity.ok(stationService.searchStations(q));
    }

    @GetMapping("/{stationId}")
    @PreAuthorize("hasAuthority('STATION_READ')")
    public ResponseEntity<StationDetailsResponse> getStationDetails(@PathVariable UUID stationId) {
        return ResponseEntity.ok(stationService.getStationDetails(stationId));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('STATION_CREATE')")
    public ResponseEntity<StationResponse> createStation(@Valid @RequestBody CreateStationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(stationService.createStation(request));
    }

    @PutMapping("/{stationId}")
    @PreAuthorize("hasAuthority('STATION_UPDATE')")
    public ResponseEntity<StationResponse> updateStation(
        @PathVariable UUID stationId,
        @Valid @RequestBody UpdateStationRequest request
    ) {
        return ResponseEntity.ok(stationService.updateStation(stationId, request));
    }

    @PatchMapping("/{stationId}/verify")
    @PreAuthorize("hasAuthority('STATION_VERIFY')")
    public ResponseEntity<StationResponse> verifyStation(@PathVariable UUID stationId) {
        return ResponseEntity.ok(stationService.verifyStation(stationId));
    }

    @PatchMapping("/{stationId}/status")
    @PreAuthorize("hasAuthority('STATION_UPDATE')")
    public ResponseEntity<StationResponse> toggleStationStatus(@PathVariable UUID stationId) {
        return ResponseEntity.ok(stationService.toggleStationStatus(stationId));
    }

    @GetMapping("/{stationId}/contacts")
    @PreAuthorize("hasAuthority('STATION_CONTACT_READ')")
    public ResponseEntity<List<StationContactResponse>> getStationContacts(@PathVariable UUID stationId) {
        return ResponseEntity.ok(stationService.getStationContacts(stationId));
    }

    @PostMapping("/{stationId}/contacts")
    @PreAuthorize("hasAuthority('STATION_CONTACT_CREATE')")
    public ResponseEntity<StationContactResponse> createStationContact(
        @PathVariable UUID stationId,
        @Valid @RequestBody CreateStationContactRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(stationService.createStationContact(stationId, request));
    }

    @PutMapping("/contacts/{contactId}")
    @PreAuthorize("hasAuthority('STATION_CONTACT_UPDATE')")
    public ResponseEntity<StationContactResponse> updateStationContact(
        @PathVariable UUID contactId,
        @Valid @RequestBody UpdateStationContactRequest request
    ) {
        return ResponseEntity.ok(stationService.updateStationContact(contactId, request));
    }

    @PatchMapping("/contacts/{contactId}/verify")
    @PreAuthorize("hasAuthority('STATION_CONTACT_VERIFY')")
    public ResponseEntity<StationContactResponse> verifyStationContact(@PathVariable UUID contactId) {
        return ResponseEntity.ok(stationService.verifyStationContact(contactId));
    }
}

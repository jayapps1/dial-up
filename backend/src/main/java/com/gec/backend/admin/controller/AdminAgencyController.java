package com.gec.backend.admin.controller;

import com.gec.backend.admin.dto.CreateAgencyRequest;
import com.gec.backend.admin.dto.UpdateAgencyRequest;
import com.gec.backend.agency.dto.AgencyResponse;
import com.gec.backend.agency.service.AgencyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/agencies")
public class AdminAgencyController {

    private final AgencyService agencyService;

    public AdminAgencyController(AgencyService agencyService) {
        this.agencyService = agencyService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('AGENCY_READ')")
    public ResponseEntity<List<AgencyResponse>> getAgencies() {
        return ResponseEntity.ok(agencyService.getAllAgencies());
    }

    @GetMapping("/{agencyId}")
    @PreAuthorize("hasAuthority('AGENCY_READ')")
    public ResponseEntity<AgencyResponse> getAgencyById(@PathVariable UUID agencyId) {
        return ResponseEntity.ok(agencyService.getAgencyById(agencyId));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('AGENCY_CREATE')")
    public ResponseEntity<AgencyResponse> createAgency(@Valid @RequestBody CreateAgencyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(agencyService.createAgency(request));
    }

    @PutMapping("/{agencyId}")
    @PreAuthorize("hasAuthority('AGENCY_UPDATE')")
    public ResponseEntity<AgencyResponse> updateAgency(
        @PathVariable UUID agencyId,
        @Valid @RequestBody UpdateAgencyRequest request
    ) {
        return ResponseEntity.ok(agencyService.updateAgency(agencyId, request));
    }

    @PatchMapping("/{agencyId}/verify")
    @PreAuthorize("hasAuthority('AGENCY_VERIFY')")
    public ResponseEntity<AgencyResponse> verifyAgency(@PathVariable UUID agencyId) {
        return ResponseEntity.ok(agencyService.verifyAgency(agencyId));
    }

    @PatchMapping("/{agencyId}/status")
    @PreAuthorize("hasAuthority('AGENCY_UPDATE')")
    public ResponseEntity<AgencyResponse> toggleAgencyStatus(@PathVariable UUID agencyId) {
        return ResponseEntity.ok(agencyService.toggleAgencyStatus(agencyId));
    }
}

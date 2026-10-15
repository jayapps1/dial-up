package com.gec.backend.admin.controller;

import com.gec.backend.admin.dto.DashboardSummaryResponse;
import com.gec.backend.admin.dto.EmergencyActivityPoint;
import com.gec.backend.admin.service.DashboardAnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/dashboard")
public class DashboardAnalyticsController {

    private final DashboardAnalyticsService analyticsService;

    public DashboardAnalyticsController(DashboardAnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/summary")
    @PreAuthorize("hasAuthority('REPORT_READ')")
    public ResponseEntity<DashboardSummaryResponse> getSummary() {
        return ResponseEntity.ok(analyticsService.getSummary());
    }

    @GetMapping("/activity")
    @PreAuthorize("hasAuthority('REPORT_READ')")
    public ResponseEntity<List<EmergencyActivityPoint>> getActivity(@RequestParam(defaultValue = "7D") String period) {
        return ResponseEntity.ok(analyticsService.getActivity(period));
    }
}

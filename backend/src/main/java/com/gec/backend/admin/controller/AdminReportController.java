package com.gec.backend.admin.controller;

import com.gec.backend.admin.dto.*;
import com.gec.backend.admin.service.AdminReportWorkflowService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/reports")
public class AdminReportController {

    private final AdminReportWorkflowService reportWorkflowService;

    public AdminReportController(AdminReportWorkflowService reportWorkflowService) {
        this.reportWorkflowService = reportWorkflowService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('REPORT_READ')")
    public ResponseEntity<Page<AdminReportSummaryResponse>> getReports(Pageable pageable) {
        return ResponseEntity.ok(reportWorkflowService.getReports(pageable));
    }

    @GetMapping("/{reportId}")
    @PreAuthorize("hasAuthority('REPORT_READ')")
    public ResponseEntity<AdminReportDetailsResponse> getReportDetails(@PathVariable UUID reportId) {
        return ResponseEntity.ok(reportWorkflowService.getReportDetails(reportId));
    }

    @PostMapping("/{reportId}/acknowledge")
    @PreAuthorize("hasAuthority('REPORT_ACKNOWLEDGE')")
    public ResponseEntity<AdminReportDetailsResponse> acknowledgeReport(@PathVariable UUID reportId) {
        return ResponseEntity.ok(reportWorkflowService.acknowledgeReport(reportId));
    }

    @PostMapping("/{reportId}/assign")
    @PreAuthorize("hasAuthority('REPORT_ASSIGN')")
    public ResponseEntity<AdminReportDetailsResponse> assignReport(
        @PathVariable UUID reportId,
        @Valid @RequestBody ReportAssignmentRequest request
    ) {
        return ResponseEntity.ok(reportWorkflowService.assignReport(reportId, request));
    }

    @PatchMapping("/{reportId}/status")
    @PreAuthorize("hasAuthority('REPORT_UPDATE_STATUS')")
    public ResponseEntity<AdminReportDetailsResponse> updateReportStatus(
        @PathVariable UUID reportId,
        @Valid @RequestBody ReportStatusUpdateRequest request
    ) {
        return ResponseEntity.ok(reportWorkflowService.updateReportStatus(reportId, request));
    }

    @PostMapping("/{reportId}/transfer")
    @PreAuthorize("hasAuthority('REPORT_TRANSFER')")
    public ResponseEntity<AdminReportDetailsResponse> transferReport(
        @PathVariable UUID reportId,
        @Valid @RequestBody ReportTransferRequest request
    ) {
        return ResponseEntity.ok(reportWorkflowService.transferReport(reportId, request));
    }
}

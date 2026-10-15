package com.gec.backend.report.controller;

import com.gec.backend.report.dto.IncidentCategoryResponse;
import com.gec.backend.report.entity.ReportMode;
import com.gec.backend.report.service.IncidentReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/public/incident-categories")
public class PublicIncidentCategoryController {

    private final IncidentReportService reportService;

    public PublicIncidentCategoryController(IncidentReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping
    public ResponseEntity<List<IncidentCategoryResponse>> getCategories(@RequestParam(required = false) ReportMode mode) {
        return ResponseEntity.ok(reportService.getCategories(mode));
    }
}

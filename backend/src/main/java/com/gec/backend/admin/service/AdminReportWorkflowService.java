package com.gec.backend.admin.service;

import com.gec.backend.admin.dto.*;
import com.gec.backend.admin.security.AdminScopeService;
import com.gec.backend.agency.entity.AgencyEntity;
import com.gec.backend.agency.entity.AgencyType;
import com.gec.backend.agency.repository.AgencyRepository;
import com.gec.backend.common.exception.ResourceNotFoundException;
import com.gec.backend.report.entity.*;
import com.gec.backend.report.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminReportWorkflowService {

    private final IncidentReportRepository reportRepository;
    private final IncidentCategoryRepository categoryRepository;
    private final ReportAssignmentRepository assignmentRepository;
    private final ReportStatusHistoryRepository historyRepository;
    private final AgencyRepository agencyRepository;
    private final AdminScopeService scopeService;
    private final AuditService auditService;

    public AdminReportWorkflowService(
        IncidentReportRepository reportRepository,
        IncidentCategoryRepository categoryRepository,
        ReportAssignmentRepository assignmentRepository,
        ReportStatusHistoryRepository historyRepository,
        AgencyRepository agencyRepository,
        AdminScopeService scopeService,
        AuditService auditService
    ) {
        this.reportRepository = reportRepository;
        this.categoryRepository = categoryRepository;
        this.assignmentRepository = assignmentRepository;
        this.historyRepository = historyRepository;
        this.agencyRepository = agencyRepository;
        this.scopeService = scopeService;
        this.auditService = auditService;
    }

    public Page<AdminReportSummaryResponse> getReports(Pageable pageable) {
        if (scopeService.isSystemAdmin()) {
            return reportRepository.findAll(pageable).map(this::mapToSummary);
        } else {
            UUID agencyId = scopeService.getCurrentPrincipal().getAgencyId();
            if (agencyId == null) {
                return Page.empty(pageable);
            }
            AgencyEntity agency = agencyRepository.findById(agencyId).orElse(null);
            if (agency == null) {
                return Page.empty(pageable);
            }

            Set<UUID> reportIds = assignmentRepository.findByAgencyType(agency.getType())
                .stream()
                .map(ReportAssignmentEntity::getReportId)
                .collect(Collectors.toSet());

            if (reportIds.isEmpty()) {
                return Page.empty(pageable);
            }

            return reportRepository.findByIdIn(reportIds, pageable).map(this::mapToSummary);
        }
    }

    public AdminReportDetailsResponse getReportDetails(UUID reportId) {
        IncidentReportEntity report = reportRepository.findById(reportId)
            .orElseThrow(() -> new ResourceNotFoundException("Report not found: " + reportId));

        if (!scopeService.isSystemAdmin()) {
            UUID agencyId = scopeService.getCurrentPrincipal().getAgencyId();
            AgencyEntity agency = agencyId != null ? agencyRepository.findById(agencyId).orElse(null) : null;
            if (agency != null) {
                boolean assigned = assignmentRepository.findByReportId(reportId)
                    .stream()
                    .anyMatch(a -> a.getAgencyType() == agency.getType());
                if (!assigned) {
                    throw new ResourceNotFoundException("Report not found: " + reportId);
                }
            }
        }

        IncidentCategoryEntity category = categoryRepository.findById(report.getCategoryId()).orElse(null);
        String catName = category != null ? category.getName() : "Incident";

        List<ReportAssignmentEntity> assignments = assignmentRepository.findByReportId(reportId);
        Set<AgencyType> targetAgencies = new HashSet<>();
        assignments.forEach(a -> targetAgencies.add(a.getAgencyType()));

        List<AdminReportDetailsResponse.StatusHistoryDto> historyDtos = historyRepository
            .findByReportIdOrderByCreatedAtAsc(reportId)
            .stream()
            .map(h -> new AdminReportDetailsResponse.StatusHistoryDto(h.getStatus(), h.getNote(), h.getCreatedAt()))
            .toList();

        return new AdminReportDetailsResponse(
            report.getId(),
            report.getPublicReference(),
            report.getCategoryId(),
            catName,
            report.getReportMode(),
            report.getSeverity(),
            report.getStatus(),
            report.getDescription(),
            report.getLatitude(),
            report.getLongitude(),
            report.getLocationAccuracyMeters(),
            report.getLandmark(),
            report.getLocality(),
            report.getDistrict(),
            report.getRegion(),
            report.getVehicleRegistrationNumber(),
            report.getVehicleType(),
            report.getVehicleColour(),
            report.getDirectionOfTravel(),
            report.getCreatedAt(),
            report.getUpdatedAt(),
            targetAgencies,
            historyDtos
        );
    }

    @Transactional
    public AdminReportDetailsResponse acknowledgeReport(UUID reportId) {
        IncidentReportEntity report = reportRepository.findById(reportId)
            .orElseThrow(() -> new ResourceNotFoundException("Report not found: " + reportId));

        report.setStatus(ReportStatus.ACKNOWLEDGED);
        report.setUpdatedAt(LocalDateTime.now());
        reportRepository.save(report);

        UUID actorId = scopeService.getCurrentPrincipal().getId();
        ReportStatusHistoryEntity history = new ReportStatusHistoryEntity(
            UUID.randomUUID(), reportId, ReportStatus.ACKNOWLEDGED, "Report acknowledged by admin user."
        );
        historyRepository.save(history);

        auditService.logAction(actorId, "REPORT_ACKNOWLEDGED", "INCIDENT_REPORT", reportId, scopeService.getCurrentPrincipal().getAgencyId(), "Acknowledged report " + report.getPublicReference(), null, null);

        return getReportDetails(reportId);
    }

    @Transactional
    public AdminReportDetailsResponse assignReport(UUID reportId, ReportAssignmentRequest request) {
        IncidentReportEntity report = reportRepository.findById(reportId)
            .orElseThrow(() -> new ResourceNotFoundException("Report not found: " + reportId));

        report.setStatus(ReportStatus.ASSIGNED);
        report.setUpdatedAt(LocalDateTime.now());
        reportRepository.save(report);

        UUID actorId = scopeService.getCurrentPrincipal().getId();
        String note = request.note() != null ? request.note() : "Report assigned to station: " + request.stationId();

        ReportStatusHistoryEntity history = new ReportStatusHistoryEntity(
            UUID.randomUUID(), reportId, ReportStatus.ASSIGNED, note
        );
        historyRepository.save(history);

        auditService.logAction(actorId, "REPORT_ASSIGNED", "INCIDENT_REPORT", reportId, scopeService.getCurrentPrincipal().getAgencyId(), "Assigned report " + report.getPublicReference(), null, null);

        return getReportDetails(reportId);
    }

    @Transactional
    public AdminReportDetailsResponse updateReportStatus(UUID reportId, ReportStatusUpdateRequest request) {
        IncidentReportEntity report = reportRepository.findById(reportId)
            .orElseThrow(() -> new ResourceNotFoundException("Report not found: " + reportId));

        validateStatusTransition(report.getStatus(), request.status());

        report.setStatus(request.status());
        report.setUpdatedAt(LocalDateTime.now());
        reportRepository.save(report);

        UUID actorId = scopeService.getCurrentPrincipal().getId();
        ReportStatusHistoryEntity history = new ReportStatusHistoryEntity(
            UUID.randomUUID(), reportId, request.status(), request.note() != null ? request.note() : "Status updated to " + request.status().name()
        );
        historyRepository.save(history);

        auditService.logAction(actorId, "REPORT_STATUS_CHANGED", "INCIDENT_REPORT", reportId, scopeService.getCurrentPrincipal().getAgencyId(), "Status updated to " + request.status().name() + " for " + report.getPublicReference(), null, null);

        return getReportDetails(reportId);
    }

    @Transactional
    public AdminReportDetailsResponse transferReport(UUID reportId, ReportTransferRequest request) {
        IncidentReportEntity report = reportRepository.findById(reportId)
            .orElseThrow(() -> new ResourceNotFoundException("Report not found: " + reportId));

        report.setStatus(ReportStatus.TRANSFERRED);
        report.setUpdatedAt(LocalDateTime.now());
        reportRepository.save(report);

        ReportAssignmentEntity newAssignment = new ReportAssignmentEntity(
            UUID.randomUUID(), reportId, request.targetAgencyType(), ReportStatus.ASSIGNED
        );
        assignmentRepository.save(newAssignment);

        UUID actorId = scopeService.getCurrentPrincipal().getId();
        String note = request.note() != null ? request.note() : "Transferred report to agency: " + request.targetAgencyType().name();
        ReportStatusHistoryEntity history = new ReportStatusHistoryEntity(
            UUID.randomUUID(), reportId, ReportStatus.TRANSFERRED, note
        );
        historyRepository.save(history);

        auditService.logAction(actorId, "REPORT_TRANSFERRED", "INCIDENT_REPORT", reportId, scopeService.getCurrentPrincipal().getAgencyId(), "Transferred report " + report.getPublicReference() + " to " + request.targetAgencyType().name(), null, null);

        return getReportDetails(reportId);
    }

    private void validateStatusTransition(ReportStatus current, ReportStatus target) {
        if (current == target) return;
        if (current == ReportStatus.CLOSED || current == ReportStatus.REJECTED) {
            throw new IllegalArgumentException("Cannot transition from closed/rejected status " + current);
        }
    }

    private AdminReportSummaryResponse mapToSummary(IncidentReportEntity report) {
        IncidentCategoryEntity category = categoryRepository.findById(report.getCategoryId()).orElse(null);
        String catName = category != null ? category.getName() : "Incident";

        List<ReportAssignmentEntity> assignments = assignmentRepository.findByReportId(report.getId());
        Set<AgencyType> targetAgencies = new HashSet<>();
        assignments.forEach(a -> targetAgencies.add(a.getAgencyType()));

        return new AdminReportSummaryResponse(
            report.getId(),
            report.getPublicReference(),
            report.getCategoryId(),
            catName,
            report.getReportMode(),
            report.getSeverity(),
            report.getStatus(),
            report.getCreatedAt(),
            targetAgencies
        );
    }
}

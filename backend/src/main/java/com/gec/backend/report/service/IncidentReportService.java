package com.gec.backend.report.service;

import com.gec.backend.agency.entity.AgencyType;
import com.gec.backend.common.exception.ResourceNotFoundException;
import com.gec.backend.report.dto.*;
import com.gec.backend.report.entity.*;
import com.gec.backend.report.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class IncidentReportService {

    private final IncidentCategoryRepository categoryRepository;
    private final IncidentReportRepository reportRepository;
    private final ReportAssignmentRepository assignmentRepository;
    private final ReportStatusHistoryRepository historyRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    public IncidentReportService(
        IncidentCategoryRepository categoryRepository,
        IncidentReportRepository reportRepository,
        ReportAssignmentRepository assignmentRepository,
        ReportStatusHistoryRepository historyRepository
    ) {
        this.categoryRepository = categoryRepository;
        this.reportRepository = reportRepository;
        this.assignmentRepository = assignmentRepository;
        this.historyRepository = historyRepository;
    }

    public List<IncidentCategoryResponse> getCategories(ReportMode mode) {
        return categoryRepository.findByActiveTrue()
            .stream()
            .filter(cat -> {
                if (mode == null) return true;
                return mode == ReportMode.SILENT ? cat.isSupportsSilent() : cat.isSupportsGeneral();
            })
            .map(cat -> new IncidentCategoryResponse(
                cat.getId(),
                cat.getCode(),
                cat.getName(),
                cat.getDescription(),
                cat.getDefaultSeverity(),
                cat.isSupportsSilent(),
                cat.isSupportsGeneral(),
                cat.isSupportsCall(),
                cat.getDefaultAgencyTypes()
            ))
            .toList();
    }

    @Transactional
    public IncidentReportResponse submitReport(IncidentReportRequest request) {
        if (request.clientGeneratedRequestId() != null) {
            Optional<IncidentReportEntity> existingOpt = reportRepository.findByClientGeneratedRequestId(request.clientGeneratedRequestId());
            if (existingOpt.isPresent()) {
                IncidentReportEntity existing = existingOpt.get();
                List<ReportAssignmentEntity> assignments = assignmentRepository.findByReportId(existing.getId());
                Set<AgencyType> targetAgencies = new HashSet<>();
                assignments.forEach(a -> targetAgencies.add(a.getAgencyType()));

                return new IncidentReportResponse(
                    existing.getId(),
                    existing.getPublicReference(),
                    null,
                    existing.getStatus(),
                    existing.getReportMode(),
                    existing.getSeverity(),
                    existing.getCreatedAt(),
                    targetAgencies
                );
            }
        }

        IncidentCategoryEntity category = categoryRepository.findByIdAndActiveTrue(request.categoryId())
            .orElseThrow(() -> new ResourceNotFoundException("Incident category not found or inactive: " + request.categoryId()));

        if (request.reportMode() == ReportMode.SILENT && !category.isSupportsSilent()) {
            throw new IllegalArgumentException("Category does not support silent reporting.");
        }

        if (request.location() != null && request.location().latitude() != null && request.location().longitude() != null) {
            double lat = request.location().latitude();
            double lon = request.location().longitude();
            if (lat < -90.0 || lat > 90.0 || lon < -180.0 || lon > 180.0) {
                throw new IllegalArgumentException("Invalid geographic coordinates.");
            }
        }

        UUID reportId = UUID.randomUUID();
        String publicRef = generatePublicReference();
        String rawToken = generateRawToken();
        String tokenHash = hashToken(rawToken);

        IncidentReportEntity report = new IncidentReportEntity();
        report.setId(reportId);
        report.setPublicReference(publicRef);
        report.setCategoryId(category.getId());
        report.setReportMode(request.reportMode());
        report.setSeverity(category.getDefaultSeverity());
        report.setStatus(ReportStatus.SUBMITTED);
        report.setDescription(request.description());
        report.setAnonymousSessionId(request.anonymousSessionId());
        report.setTrackingTokenHash(tokenHash);
        report.setClientGeneratedRequestId(request.clientGeneratedRequestId());

        if (request.location() != null) {
            report.setLatitude(request.location().latitude());
            report.setLongitude(request.location().longitude());
            report.setLocationAccuracyMeters(request.location().accuracyMeters());
            report.setLandmark(request.location().landmark());
            report.setLocality(request.location().locality());
            report.setDistrict(request.location().district());
            report.setRegion(request.location().region());
        }

        if (request.vehicleInfo() != null) {
            report.setVehicleRegistrationNumber(request.vehicleInfo().registrationNumber());
            report.setVehicleType(request.vehicleInfo().vehicleType());
            report.setVehicleColour(request.vehicleInfo().colour());
            report.setDirectionOfTravel(request.vehicleInfo().directionOfTravel());
        }

        reportRepository.save(report);

        Set<AgencyType> targetAgencies = category.getDefaultAgencyTypes();
        for (AgencyType agencyType : targetAgencies) {
            ReportAssignmentEntity assignment = new ReportAssignmentEntity(
                UUID.randomUUID(),
                reportId,
                agencyType,
                ReportStatus.ASSIGNED
            );
            assignmentRepository.save(assignment);
        }

        ReportStatusHistoryEntity history = new ReportStatusHistoryEntity(
            UUID.randomUUID(),
            reportId,
            ReportStatus.SUBMITTED,
            "Incident report received via public API."
        );
        historyRepository.save(history);

        return new IncidentReportResponse(
            reportId,
            publicRef,
            rawToken,
            ReportStatus.SUBMITTED,
            request.reportMode(),
            category.getDefaultSeverity(),
            report.getCreatedAt(),
            targetAgencies
        );
    }

    public TrackedReportResponse trackReport(String publicReference, String rawToken) {
        IncidentReportEntity report = reportRepository.findByPublicReference(publicReference)
            .orElseThrow(() -> new ResourceNotFoundException("Report not found: " + publicReference));

        if (report.getTrackingTokenHash() == null || !report.getTrackingTokenHash().equals(hashToken(rawToken))) {
            throw new IllegalArgumentException("Invalid tracking token for reference: " + publicReference);
        }

        IncidentCategoryEntity category = categoryRepository.findById(report.getCategoryId()).orElse(null);
        String categoryName = category != null ? category.getName() : "Incident";

        List<ReportAssignmentEntity> assignments = assignmentRepository.findByReportId(report.getId());
        Set<AgencyType> targetAgencies = new HashSet<>();
        assignments.forEach(a -> targetAgencies.add(a.getAgencyType()));

        List<TrackedReportResponse.ReportStatusHistoryDto> historyDtos = historyRepository
            .findByReportIdOrderByCreatedAtAsc(report.getId())
            .stream()
            .map(h -> new TrackedReportResponse.ReportStatusHistoryDto(h.getStatus(), h.getNote(), h.getCreatedAt()))
            .toList();

        return new TrackedReportResponse(
            report.getPublicReference(),
            categoryName,
            report.getStatus(),
            report.getCreatedAt(),
            report.getUpdatedAt(),
            targetAgencies,
            historyDtos
        );
    }

    private String generatePublicReference() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String suffix = UUID.randomUUID().toString().substring(0, 6).toUpperCase(Locale.US);
        return "RPT-" + datePart + "-" + suffix;
    }

    private String generateRawToken() {
        byte[] bytes = new byte[24];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hashToken(String rawToken) {
        if (rawToken == null) return null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm unavailable", e);
        }
    }
}

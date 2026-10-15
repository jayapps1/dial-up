package com.gec.backend.admin.service;

import com.gec.backend.admin.dto.DashboardSummaryResponse;
import com.gec.backend.admin.dto.EmergencyActivityPoint;
import com.gec.backend.admin.security.AdminScopeService;
import com.gec.backend.agency.repository.AgencyRepository;
import com.gec.backend.report.entity.IncidentReportEntity;
import com.gec.backend.report.entity.IncidentSeverity;
import com.gec.backend.report.entity.ReportStatus;
import com.gec.backend.report.repository.IncidentReportRepository;
import com.gec.backend.station.repository.StationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class DashboardAnalyticsService {

    private final AgencyRepository agencyRepository;
    private final StationRepository stationRepository;
    private final IncidentReportRepository reportRepository;
    private final AdminScopeService scopeService;

    public DashboardAnalyticsService(
        AgencyRepository agencyRepository,
        StationRepository stationRepository,
        IncidentReportRepository reportRepository,
        AdminScopeService scopeService
    ) {
        this.agencyRepository = agencyRepository;
        this.stationRepository = stationRepository;
        this.reportRepository = reportRepository;
        this.scopeService = scopeService;
    }

    public DashboardSummaryResponse getSummary() {
        long totalAgencies = agencyRepository.count();
        long activeStations = stationRepository.count();
        List<IncidentReportEntity> allReports = reportRepository.findAll();

        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        long reportsToday = allReports.stream().filter(r -> r.getCreatedAt().isAfter(startOfToday)).count();
        long openIncidents = allReports.stream().filter(r -> r.getStatus() != ReportStatus.CLOSED && r.getStatus() != ReportStatus.RESOLVED && r.getStatus() != ReportStatus.REJECTED).count();
        long criticalIncidents = allReports.stream().filter(r -> r.getSeverity() == IncidentSeverity.CRITICAL && r.getStatus() != ReportStatus.CLOSED).count();
        long unacknowledged = allReports.stream().filter(r -> r.getStatus() == ReportStatus.SUBMITTED).count();

        return new DashboardSummaryResponse(
            totalAgencies,
            activeStations,
            reportsToday,
            openIncidents,
            criticalIncidents,
            unacknowledged
        );
    }

    public List<EmergencyActivityPoint> getActivity(String period) {
        List<IncidentReportEntity> reports = reportRepository.findAll();
        List<EmergencyActivityPoint> points = new ArrayList<>();

        if ("24H".equalsIgnoreCase(period)) {
            LocalDateTime now = LocalDateTime.now();
            for (int i = 23; i >= 0; i--) {
                LocalDateTime hourStart = now.minusHours(i).withMinute(0).withSecond(0);
                LocalDateTime hourEnd = hourStart.plusHours(1);
                long count = reports.stream().filter(r -> !r.getCreatedAt().isBefore(hourStart) && r.getCreatedAt().isBefore(hourEnd)).count();
                points.add(new EmergencyActivityPoint(hourStart.format(DateTimeFormatter.ofPattern("HH:00")), count));
            }
        } else if ("7D".equalsIgnoreCase(period)) {
            LocalDate today = LocalDate.now();
            for (int i = 6; i >= 0; i--) {
                LocalDate date = today.minusDays(i);
                long count = reports.stream().filter(r -> r.getCreatedAt().toLocalDate().equals(date)).count();
                points.add(new EmergencyActivityPoint(date.format(DateTimeFormatter.ofPattern("EEE")), count));
            }
        } else if ("30D".equalsIgnoreCase(period)) {
            LocalDate today = LocalDate.now();
            for (int i = 29; i >= 0; i -= 5) {
                LocalDate date = today.minusDays(i);
                long count = reports.stream().filter(r -> !r.getCreatedAt().toLocalDate().isBefore(date.minusDays(4)) && !r.getCreatedAt().toLocalDate().isAfter(date)).count();
                points.add(new EmergencyActivityPoint(date.format(DateTimeFormatter.ofPattern("dd MMM")), count));
            }
        } else {
            LocalDate today = LocalDate.now();
            for (int i = 11; i >= 0; i--) {
                LocalDate month = today.minusMonths(i);
                long count = reports.stream().filter(r -> r.getCreatedAt().getMonth() == month.getMonth() && r.getCreatedAt().getYear() == month.getYear()).count();
                points.add(new EmergencyActivityPoint(month.format(DateTimeFormatter.ofPattern("MMM")), count));
            }
        }

        return points;
    }
}

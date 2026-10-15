package com.gec.backend.admin.dto;

public record DashboardSummaryResponse(
    long totalAgencies,
    long activeStations,
    long reportsToday,
    long openIncidents,
    long criticalIncidents,
    long unacknowledgedReports
) {}

package com.gec.backend.admin.service;

import com.gec.backend.admin.dto.AdminReportDetailsResponse;
import com.gec.backend.admin.entity.AdminUserEntity;
import com.gec.backend.admin.security.AdminScopeService;
import com.gec.backend.admin.security.AdminUserPrincipal;
import com.gec.backend.agency.repository.AgencyRepository;
import com.gec.backend.report.entity.*;
import com.gec.backend.report.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

class AdminReportWorkflowServiceTest {

    private final IncidentReportRepository reportRepository = Mockito.mock(IncidentReportRepository.class);
    private final IncidentCategoryRepository categoryRepository = Mockito.mock(IncidentCategoryRepository.class);
    private final ReportAssignmentRepository assignmentRepository = Mockito.mock(ReportAssignmentRepository.class);
    private final ReportStatusHistoryRepository historyRepository = Mockito.mock(ReportStatusHistoryRepository.class);
    private final AgencyRepository agencyRepository = Mockito.mock(AgencyRepository.class);
    private final AdminScopeService scopeService = Mockito.mock(AdminScopeService.class);
    private final AuditService auditService = Mockito.mock(AuditService.class);

    private final AdminReportWorkflowService workflowService = new AdminReportWorkflowService(
        reportRepository, categoryRepository, assignmentRepository, historyRepository, agencyRepository, scopeService, auditService
    );

    private final UUID reportId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        AdminUserEntity admin = new AdminUserEntity();
        admin.setId(UUID.randomUUID());
        admin.setEmail("op@gec.gov.gh");
        admin.setPasswordHash("hash");
        admin.setActive(true);
        admin.setRoles(Set.of());

        AdminUserPrincipal principal = new AdminUserPrincipal(admin);
        given(scopeService.getCurrentPrincipal()).willReturn(principal);
        given(scopeService.isSystemAdmin()).willReturn(true);
    }

    @Test
    void acknowledgeReport_updatesStatusToAcknowledged() {
        IncidentReportEntity report = new IncidentReportEntity();
        report.setId(reportId);
        report.setPublicReference("RPT-20261014-TEST01");
        report.setCategoryId(UUID.randomUUID());
        report.setReportMode(ReportMode.SILENT);
        report.setSeverity(IncidentSeverity.HIGH);
        report.setStatus(ReportStatus.SUBMITTED);

        given(reportRepository.findById(reportId)).willReturn(Optional.of(report));
        given(assignmentRepository.findByReportId(reportId)).willReturn(List.of());
        given(historyRepository.findByReportIdOrderByCreatedAtAsc(reportId)).willReturn(List.of());

        AdminReportDetailsResponse result = workflowService.acknowledgeReport(reportId);

        assertNotNull(result);
        assertEquals(ReportStatus.ACKNOWLEDGED, result.status());
    }
}

package com.gec.backend.agency.service;

import com.gec.backend.admin.entity.AdminUserEntity;
import com.gec.backend.admin.security.AdminScopeService;
import com.gec.backend.admin.security.AdminUserPrincipal;
import com.gec.backend.admin.service.AuditService;
import com.gec.backend.agency.dto.AgencyResponse;
import com.gec.backend.agency.entity.AgencyEntity;
import com.gec.backend.agency.entity.AgencyType;
import com.gec.backend.agency.mapper.AgencyMapper;
import com.gec.backend.agency.repository.AgencyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

class AgencyScopeSecurityTest {

    private final AgencyRepository agencyRepository = Mockito.mock(AgencyRepository.class);
    private final AgencyMapper agencyMapper = new AgencyMapper();
    private final AdminScopeService scopeService = Mockito.mock(AdminScopeService.class);
    private final AuditService auditService = Mockito.mock(AuditService.class);

    private final AgencyService agencyService = new AgencyService(
        agencyRepository, agencyMapper, scopeService, auditService
    );

    private final UUID agencyId = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        AdminUserEntity admin = new AdminUserEntity();
        admin.setId(userId);
        admin.setEmail("agencyadmin@gnfs.gov.gh");
        admin.setAgencyId(agencyId);
        admin.setRoles(Set.of());

        AdminUserPrincipal principal = new AdminUserPrincipal(admin);
        given(scopeService.getCurrentPrincipal()).willReturn(principal);
        given(scopeService.isSystemAdmin()).willReturn(false);
    }

    @Test
    void getAllAgencies_returnsOnlyAssignedAgencyForAgencyAdmin() {
        AgencyEntity agency = new AgencyEntity(
            agencyId, "Ghana National Fire Service", "GNFS", AgencyType.FIRE, "192", true, true
        );
        given(agencyRepository.findById(agencyId)).willReturn(Optional.of(agency));

        List<AgencyResponse> agencies = agencyService.getAllAgencies();

        assertNotNull(agencies);
        assertEquals(1, agencies.size());
        assertEquals("GNFS", agencies.get(0).code());
    }
}

package com.gec.backend.agency.service;

import com.gec.backend.admin.security.AdminScopeService;
import com.gec.backend.admin.service.AuditService;
import com.gec.backend.agency.dto.AgencyResponse;
import com.gec.backend.agency.entity.AgencyEntity;
import com.gec.backend.agency.entity.AgencyType;
import com.gec.backend.agency.mapper.AgencyMapper;
import com.gec.backend.agency.repository.AgencyRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;

class AgencyServiceTest {

    private final AgencyRepository agencyRepository = Mockito.mock(AgencyRepository.class);
    private final AgencyMapper agencyMapper = new AgencyMapper();
    private final AdminScopeService scopeService = Mockito.mock(AdminScopeService.class);
    private final AuditService auditService = Mockito.mock(AuditService.class);

    private final AgencyService agencyService = new AgencyService(
        agencyRepository, agencyMapper, scopeService, auditService
    );

    @Test
    void getActiveVerifiedAgencies_returnsActiveVerifiedOnly() {
        UUID id = UUID.randomUUID();
        AgencyEntity agency = new AgencyEntity(id, "Development Fire Service", "GNFS-DEV", AgencyType.FIRE, "192", true, true);
        given(agencyRepository.findByActiveTrueAndVerifiedTrue()).willReturn(List.of(agency));

        List<AgencyResponse> result = agencyService.getActiveVerifiedAgencies();

        assertEquals(1, result.size());
        assertEquals(id, result.get(0).id());
        assertEquals("GNFS-DEV", result.get(0).code());
    }

    @Test
    void getAgencyById_returnsAgency_whenActiveAndVerified() {
        UUID id = UUID.randomUUID();
        AgencyEntity agency = new AgencyEntity(id, "Development Police Service", "GPS-DEV", AgencyType.POLICE, "191", true, true);
        given(agencyRepository.findById(id)).willReturn(Optional.of(agency));

        AgencyResponse result = agencyService.getAgencyById(id);

        assertNotNull(result);
        assertEquals(id, result.id());
        assertEquals("GPS-DEV", result.code());
    }
}

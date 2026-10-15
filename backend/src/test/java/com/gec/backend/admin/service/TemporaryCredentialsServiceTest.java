package com.gec.backend.admin.service;

import com.gec.backend.admin.dto.ResetPasswordResponse;
import com.gec.backend.admin.dto.TemporaryCredentialResponse;
import com.gec.backend.admin.entity.AdminUserEntity;
import com.gec.backend.admin.repository.AdminUserRepository;
import com.gec.backend.admin.security.AdminScopeService;
import com.gec.backend.admin.security.AdminUserPrincipal;
import com.gec.backend.agency.repository.AgencyRepository;
import com.gec.backend.station.repository.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

class TemporaryCredentialsServiceTest {

    private final AdminUserRepository userRepository = Mockito.mock(AdminUserRepository.class);
    private final AgencyRepository agencyRepository = Mockito.mock(AgencyRepository.class);
    private final StationRepository stationRepository = Mockito.mock(StationRepository.class);
    private final PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
    private final AdminScopeService scopeService = Mockito.mock(AdminScopeService.class);
    private final AuditService auditService = Mockito.mock(AuditService.class);

    private final TemporaryCredentialsService credentialsService = new TemporaryCredentialsService(
        userRepository, agencyRepository, stationRepository, passwordEncoder, scopeService, auditService
    );

    private final UUID agencyId = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        AdminUserEntity admin = new AdminUserEntity();
        admin.setId(userId);
        admin.setEmail("sysadmin@gec.gov.gh");
        admin.setAgencyId(agencyId);
        admin.setRoles(Set.of());

        AdminUserPrincipal principal = new AdminUserPrincipal(admin);
        given(scopeService.getCurrentPrincipal()).willReturn(principal);
        given(scopeService.isSystemAdmin()).willReturn(true);
    }

    @Test
    void getPendingCredentials_returnsUsersWithMustChangePasswordTrue() {
        AdminUserEntity user = new AdminUserEntity();
        user.setId(UUID.randomUUID());
        user.setEmail("kofi@gec.gov.gh");
        user.setFirstName("Kofi");
        user.setLastName("Annan");
        user.setMustChangePassword(true);
        user.setTemporaryPasswordIssuedAt(LocalDateTime.now());
        user.setRoles(Set.of());

        given(userRepository.findByMustChangePasswordTrue()).willReturn(List.of(user));

        List<TemporaryCredentialResponse> result = credentialsService.getPendingCredentials();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("kofi@gec.gov.gh", result.get(0).email());
        assertTrue(result.get(0).mustChangePassword());
    }

    @Test
    void resetTemporaryPassword_generatesNewTempPasswordAndReturnsOnce() {
        UUID targetUserId = UUID.randomUUID();
        AdminUserEntity targetUser = new AdminUserEntity();
        targetUser.setId(targetUserId);
        targetUser.setEmail("target@gec.gov.gh");
        targetUser.setAgencyId(agencyId);
        targetUser.setMustChangePassword(false);
        targetUser.setRoles(Set.of());

        given(userRepository.findById(targetUserId)).willReturn(Optional.of(targetUser));
        given(passwordEncoder.encode(any())).willReturn("newEncodedHash");

        ResetPasswordResponse response = credentialsService.resetTemporaryPassword(targetUserId);

        assertNotNull(response);
        assertEquals("target@gec.gov.gh", response.email());
        assertTrue(response.mustChangePassword());
        assertNotNull(response.temporaryPassword());
        assertTrue(response.temporaryPassword().startsWith("Gec!"));
    }
}

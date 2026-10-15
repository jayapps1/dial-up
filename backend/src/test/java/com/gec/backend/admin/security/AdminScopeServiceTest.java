package com.gec.backend.admin.security;

import com.gec.backend.admin.entity.AdminUserEntity;
import com.gec.backend.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AdminScopeServiceTest {

    private final AdminScopeService scopeService = new AdminScopeService();
    private final UUID gnfsAgencyId = UUID.randomUUID();
    private final UUID policeAgencyId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        AdminUserEntity gnfsUser = new AdminUserEntity();
        gnfsUser.setId(UUID.randomUUID());
        gnfsUser.setEmail("gnfs.admin@gec.gov.gh");
        gnfsUser.setPasswordHash("hash");
        gnfsUser.setAgencyId(gnfsAgencyId);
        gnfsUser.setActive(true);
        gnfsUser.setRoles(Set.of());

        AdminUserPrincipal principal = new AdminUserPrincipal(gnfsUser);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void validateAgencyScope_allowsSameAgency() {
        assertDoesNotThrow(() -> scopeService.validateAgencyScope(gnfsAgencyId));
    }

    @Test
    void validateAgencyScope_rejectsDifferentAgency() {
        assertThrows(ResourceNotFoundException.class, () -> {
            scopeService.validateAgencyScope(policeAgencyId);
        });
    }
}

package com.gec.backend.admin.security;

import com.gec.backend.common.exception.ResourceNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AdminScopeService {

    public AdminUserPrincipal getCurrentPrincipal() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof AdminUserPrincipal principal)) {
            throw new AccessDeniedException("Unauthenticated admin user.");
        }
        return principal;
    }

    public boolean isSystemAdmin() {
        AdminUserPrincipal principal = getCurrentPrincipal();
        return principal.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_SYSTEM_ADMIN"));
    }

    public void validateAgencyScope(UUID targetAgencyId) {
        if (isSystemAdmin()) return;

        AdminUserPrincipal principal = getCurrentPrincipal();
        if (principal.getAgencyId() == null || targetAgencyId == null || !principal.getAgencyId().equals(targetAgencyId)) {
            throw new ResourceNotFoundException("Resource not found or access denied.");
        }
    }

    public void validateStationScope(UUID targetStationAgencyId, UUID targetStationId) {
        validateAgencyScope(targetStationAgencyId);

        AdminUserPrincipal principal = getCurrentPrincipal();
        if (!isSystemAdmin() && principal.getStationId() != null && targetStationId != null) {
            if (!principal.getStationId().equals(targetStationId)) {
                throw new ResourceNotFoundException("Station not found or access denied.");
            }
        }
    }
}

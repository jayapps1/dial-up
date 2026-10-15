package com.gec.backend.admin.service;

import com.gec.backend.admin.dto.ResetPasswordResponse;
import com.gec.backend.admin.dto.TemporaryCredentialResponse;
import com.gec.backend.admin.entity.AdminUserEntity;
import com.gec.backend.admin.entity.RoleEntity;
import com.gec.backend.admin.repository.AdminUserRepository;
import com.gec.backend.admin.security.AdminScopeService;
import com.gec.backend.agency.entity.AgencyEntity;
import com.gec.backend.agency.repository.AgencyRepository;
import com.gec.backend.common.exception.ResourceNotFoundException;
import com.gec.backend.station.entity.StationEntity;
import com.gec.backend.station.repository.StationRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TemporaryCredentialsService {

    private final AdminUserRepository userRepository;
    private final AgencyRepository agencyRepository;
    private final StationRepository stationRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminScopeService scopeService;
    private final AuditService auditService;
    private final SecureRandom secureRandom = new SecureRandom();

    public TemporaryCredentialsService(
        AdminUserRepository userRepository,
        AgencyRepository agencyRepository,
        StationRepository stationRepository,
        PasswordEncoder passwordEncoder,
        AdminScopeService scopeService,
        AuditService auditService
    ) {
        this.userRepository = userRepository;
        this.agencyRepository = agencyRepository;
        this.stationRepository = stationRepository;
        this.passwordEncoder = passwordEncoder;
        this.scopeService = scopeService;
        this.auditService = auditService;
    }

    public List<TemporaryCredentialResponse> getPendingCredentials() {
        List<AdminUserEntity> users;
        if (scopeService.isSystemAdmin()) {
            users = userRepository.findByMustChangePasswordTrue();
        } else {
            UUID agencyId = scopeService.getCurrentPrincipal().getAgencyId();
            users = userRepository.findByAgencyIdAndMustChangePasswordTrue(agencyId);
        }

        return users.stream().map(this::mapToCredentialResponse).toList();
    }

    @Transactional
    public ResetPasswordResponse resetTemporaryPassword(UUID targetUserId) {
        AdminUserEntity user = userRepository.findById(targetUserId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + targetUserId));

        if (user.getAgencyId() != null) {
            scopeService.validateAgencyScope(user.getAgencyId());
        } else if (!scopeService.isSystemAdmin()) {
            throw new ResourceNotFoundException("User not found: " + targetUserId);
        }

        String tempPassword = generateTempPassword();
        LocalDateTime issuedAt = LocalDateTime.now();
        LocalDateTime expiresAt = issuedAt.plusHours(48);

        user.setPasswordHash(passwordEncoder.encode(tempPassword));
        user.setMustChangePassword(true);
        user.setTemporaryPasswordIssuedAt(issuedAt);
        user.setUpdatedAt(issuedAt);

        userRepository.save(user);

        UUID actorId = scopeService.getCurrentPrincipal().getId();
        auditService.logAction(actorId, "TEMPORARY_PASSWORD_RESET", "ADMIN_USER", targetUserId, user.getAgencyId(), "Reset temporary password for " + user.getEmail(), null, null);

        return new ResetPasswordResponse(
            user.getId(),
            user.getEmail(),
            tempPassword,
            true,
            issuedAt,
            expiresAt
        );
    }

    private TemporaryCredentialResponse mapToCredentialResponse(AdminUserEntity user) {
        Set<String> roleCodes = user.getRoles().stream().map(RoleEntity::getCode).collect(Collectors.toSet());
        LocalDateTime issuedAt = user.getTemporaryPasswordIssuedAt() != null ? user.getTemporaryPasswordIssuedAt() : user.getCreatedAt();
        LocalDateTime expiresAt = issuedAt != null ? issuedAt.plusHours(48) : null;
        String agencyName = user.getAgencyId() != null ? agencyRepository.findById(user.getAgencyId()).map(AgencyEntity::getName).orElse(null) : null;
        String stationName = user.getStationId() != null ? stationRepository.findById(user.getStationId()).map(StationEntity::getName).orElse(null) : null;

        return new TemporaryCredentialResponse(
            user.getId(),
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            roleCodes,
            user.getAgencyId(),
            agencyName,
            user.getRegion(),
            user.getStationId(),
            stationName,
            user.isMustChangePassword(),
            issuedAt,
            expiresAt,
            user.getLastLoginAt()
        );
    }

    private String generateTempPassword() {
        byte[] bytes = new byte[8];
        secureRandom.nextBytes(bytes);
        return "Gec!" + Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}

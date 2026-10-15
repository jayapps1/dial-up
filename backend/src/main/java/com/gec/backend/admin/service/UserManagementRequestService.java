package com.gec.backend.admin.service;

import com.gec.backend.admin.dto.CreateUserManagementRequest;
import com.gec.backend.admin.dto.UserManagementRequestResponse;
import com.gec.backend.admin.entity.AdminUserEntity;
import com.gec.backend.admin.entity.UserManagementRequestEntity;
import com.gec.backend.admin.repository.AdminUserRepository;
import com.gec.backend.admin.repository.UserManagementRequestRepository;
import com.gec.backend.admin.security.AdminScopeService;
import com.gec.backend.agency.entity.AgencyEntity;
import com.gec.backend.agency.repository.AgencyRepository;
import com.gec.backend.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UserManagementRequestService {

    private final UserManagementRequestRepository requestRepository;
    private final AdminUserRepository userRepository;
    private final AgencyRepository agencyRepository;
    private final AdminUserService userService;
    private final AdminScopeService scopeService;
    private final AuditService auditService;

    public UserManagementRequestService(
        UserManagementRequestRepository requestRepository,
        AdminUserRepository userRepository,
        AgencyRepository agencyRepository,
        AdminUserService userService,
        AdminScopeService scopeService,
        AuditService auditService
    ) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.agencyRepository = agencyRepository;
        this.userService = userService;
        this.scopeService = scopeService;
        this.auditService = auditService;
    }

    public List<UserManagementRequestResponse> getRequests() {
        if (scopeService.isSystemAdmin()) {
            return requestRepository.findAll().stream().map(this::mapToResponse).toList();
        } else {
            UUID agencyId = scopeService.getCurrentPrincipal().getAgencyId();
            return requestRepository.findByAgencyIdOrderByCreatedAtDesc(agencyId).stream().map(this::mapToResponse).toList();
        }
    }

    @Transactional
    public UserManagementRequestResponse submitRequest(CreateUserManagementRequest request) {
        UUID agencyId = scopeService.getCurrentPrincipal().getAgencyId();
        UUID actorId = scopeService.getCurrentPrincipal().getId();

        UUID requestId = UUID.randomUUID();
        UserManagementRequestEntity entity = new UserManagementRequestEntity(
            requestId,
            request.requestType(),
            request.targetUserId(),
            agencyId,
            request.reason(),
            request.effectiveDate(),
            actorId
        );
        requestRepository.save(entity);

        auditService.logAction(actorId, "USER_MANAGEMENT_REQUEST_CREATED", "USER_MANAGEMENT_REQUEST", requestId, agencyId, "Submitted user removal request for user " + request.targetUserId(), null, null);

        return mapToResponse(entity);
    }

    @Transactional
    public UserManagementRequestResponse approveRequest(UUID requestId) {
        UserManagementRequestEntity entity = requestRepository.findById(requestId)
            .orElseThrow(() -> new ResourceNotFoundException("Request not found: " + requestId));

        if (!"PENDING".equals(entity.getStatus())) {
            throw new IllegalStateException("Request is not in PENDING status.");
        }

        UUID reviewerId = scopeService.getCurrentPrincipal().getId();
        entity.setStatus("APPROVED");
        entity.setReviewedBy(reviewerId);
        entity.setReviewedAt(LocalDateTime.now());
        requestRepository.save(entity);

        // Deactivate the target user
        userService.deactivateUser(entity.getTargetUserId());

        auditService.logAction(reviewerId, "USER_MANAGEMENT_REQUEST_APPROVED", "USER_MANAGEMENT_REQUEST", requestId, entity.getAgencyId(), "Approved user deactivation request for " + entity.getTargetUserId(), null, null);

        return mapToResponse(entity);
    }

    @Transactional
    public UserManagementRequestResponse rejectRequest(UUID requestId, String rejectionReason) {
        UserManagementRequestEntity entity = requestRepository.findById(requestId)
            .orElseThrow(() -> new ResourceNotFoundException("Request not found: " + requestId));

        if (!"PENDING".equals(entity.getStatus())) {
            throw new IllegalStateException("Request is not in PENDING status.");
        }

        UUID reviewerId = scopeService.getCurrentPrincipal().getId();
        entity.setStatus("REJECTED");
        entity.setReviewedBy(reviewerId);
        entity.setReviewedAt(LocalDateTime.now());
        entity.setRejectionReason(rejectionReason);
        requestRepository.save(entity);

        auditService.logAction(reviewerId, "USER_MANAGEMENT_REQUEST_REJECTED", "USER_MANAGEMENT_REQUEST", requestId, entity.getAgencyId(), "Rejected user management request: " + rejectionReason, null, null);

        return mapToResponse(entity);
    }

    private UserManagementRequestResponse mapToResponse(UserManagementRequestEntity entity) {
        AdminUserEntity targetUser = userRepository.findById(entity.getTargetUserId()).orElse(null);
        String targetUserEmail = targetUser != null ? targetUser.getEmail() : "Unknown";
        String targetUserName = targetUser != null ? targetUser.getFirstName() + " " + targetUser.getLastName() : "Unknown";
        String agencyName = agencyRepository.findById(entity.getAgencyId()).map(AgencyEntity::getName).orElse(null);

        return new UserManagementRequestResponse(
            entity.getId(),
            entity.getRequestType(),
            entity.getTargetUserId(),
            targetUserEmail,
            targetUserName,
            entity.getAgencyId(),
            agencyName,
            entity.getReason(),
            entity.getEffectiveDate(),
            entity.getStatus(),
            entity.getRequestedBy(),
            entity.getReviewedBy(),
            entity.getReviewedAt(),
            entity.getRejectionReason(),
            entity.getCreatedAt()
        );
    }
}

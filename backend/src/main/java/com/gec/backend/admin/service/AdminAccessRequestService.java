package com.gec.backend.admin.service;

import com.gec.backend.admin.dto.*;
import com.gec.backend.admin.entity.AdminAccessRequestEntity;
import com.gec.backend.admin.repository.AdminAccessRequestRepository;
import com.gec.backend.admin.security.AdminScopeService;
import com.gec.backend.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class AdminAccessRequestService {

    private final AdminAccessRequestRepository requestRepository;
    private final AdminUserService userService;
    private final AdminScopeService scopeService;
    private final AuditService auditService;

    public AdminAccessRequestService(
        AdminAccessRequestRepository requestRepository,
        AdminUserService userService,
        AdminScopeService scopeService,
        AuditService auditService
    ) {
        this.requestRepository = requestRepository;
        this.userService = userService;
        this.scopeService = scopeService;
        this.auditService = auditService;
    }

    public List<AdminAccessRequestResponse> getRequests() {
        if (scopeService.isSystemAdmin()) {
            return requestRepository.findAll().stream().map(this::mapToResponse).toList();
        } else {
            UUID agencyId = scopeService.getCurrentPrincipal().getAgencyId();
            return requestRepository.findByAgencyIdOrderByCreatedAtDesc(agencyId).stream().map(this::mapToResponse).toList();
        }
    }

    @Transactional
    public AdminAccessRequestResponse submitRequest(CreateAdminAccessRequest request) {
        UUID agencyId = scopeService.getCurrentPrincipal().getAgencyId();
        UUID actorId = scopeService.getCurrentPrincipal().getId();

        if (requestRepository.existsByCandidateEmailIgnoreCaseAndStatus(request.candidateEmail().trim(), "PENDING")) {
            throw new IllegalArgumentException("A pending request for email " + request.candidateEmail() + " already exists.");
        }

        UUID requestId = UUID.randomUUID();
        AdminAccessRequestEntity entity = new AdminAccessRequestEntity(
            requestId,
            agencyId,
            request.candidateFirstName(),
            request.candidateLastName(),
            request.candidateEmail().trim().toLowerCase(),
            request.candidatePhone(),
            request.reason(),
            actorId
        );
        requestRepository.save(entity);

        auditService.logAction(actorId, "ADMIN_ACCESS_REQUEST_CREATED", "ADMIN_ACCESS_REQUEST", requestId, agencyId, "Submitted request for " + entity.getCandidateEmail(), null, null);

        return mapToResponse(entity);
    }

    @Transactional
    public ApproveAccessRequestResponse approveRequest(UUID requestId) {
        AdminAccessRequestEntity entity = requestRepository.findById(requestId)
            .orElseThrow(() -> new ResourceNotFoundException("Request not found: " + requestId));

        if (!"PENDING".equals(entity.getStatus())) {
            throw new IllegalStateException("Request is not in PENDING status.");
        }

        UUID reviewerId = scopeService.getCurrentPrincipal().getId();
        entity.setStatus("APPROVED");
        entity.setReviewedBy(reviewerId);
        entity.setReviewedAt(LocalDateTime.now());
        requestRepository.save(entity);

        // Create the Agency Admin user
        CreateAdminUserRequest userReq = new CreateAdminUserRequest(
            entity.getCandidateEmail(),
            entity.getCandidateFirstName(),
            entity.getCandidateLastName(),
            entity.getCandidatePhone(),
            entity.getAgencyId(),
            null,
            null,
            Set.of("AGENCY_ADMIN")
        );

        CreateUserResponse createdUserResponse = userService.createUser(userReq);

        auditService.logAction(reviewerId, "ADMIN_ACCESS_REQUEST_APPROVED", "ADMIN_ACCESS_REQUEST", requestId, entity.getAgencyId(), "Approved Agency Admin request for " + entity.getCandidateEmail(), null, null);

        return new ApproveAccessRequestResponse(mapToResponse(entity), createdUserResponse.user(), createdUserResponse.tempPassword());
    }

    @Transactional
    public AdminAccessRequestResponse rejectRequest(UUID requestId, String rejectionReason) {
        AdminAccessRequestEntity entity = requestRepository.findById(requestId)
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

        auditService.logAction(reviewerId, "ADMIN_ACCESS_REQUEST_REJECTED", "ADMIN_ACCESS_REQUEST", requestId, entity.getAgencyId(), "Rejected request: " + rejectionReason, null, null);

        return mapToResponse(entity);
    }

    private AdminAccessRequestResponse mapToResponse(AdminAccessRequestEntity entity) {
        return new AdminAccessRequestResponse(
            entity.getId(),
            entity.getRequestType(),
            entity.getAgencyId(),
            entity.getRequestedRole(),
            entity.getCandidateFirstName(),
            entity.getCandidateLastName(),
            entity.getCandidateEmail(),
            entity.getCandidatePhone(),
            entity.getReason(),
            entity.getStatus(),
            entity.getRequestedBy(),
            entity.getReviewedBy(),
            entity.getReviewedAt(),
            entity.getRejectionReason(),
            entity.getCreatedAt()
        );
    }
}

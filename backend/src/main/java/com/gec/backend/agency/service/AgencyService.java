package com.gec.backend.agency.service;

import com.gec.backend.admin.dto.CreateAgencyRequest;
import com.gec.backend.admin.dto.UpdateAgencyRequest;
import com.gec.backend.admin.security.AdminScopeService;
import com.gec.backend.admin.service.AuditService;
import com.gec.backend.agency.dto.AgencyResponse;
import com.gec.backend.agency.entity.AgencyEntity;
import com.gec.backend.agency.mapper.AgencyMapper;
import com.gec.backend.agency.repository.AgencyRepository;
import com.gec.backend.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AgencyService {

    private final AgencyRepository agencyRepository;
    private final AgencyMapper agencyMapper;
    private final AdminScopeService scopeService;
    private final AuditService auditService;

    public AgencyService(
        AgencyRepository agencyRepository,
        AgencyMapper agencyMapper,
        AdminScopeService scopeService,
        AuditService auditService
    ) {
        this.agencyRepository = agencyRepository;
        this.agencyMapper = agencyMapper;
        this.scopeService = scopeService;
        this.auditService = auditService;
    }

    public List<AgencyResponse> getActiveVerifiedAgencies() {
        return agencyRepository.findByActiveTrueAndVerifiedTrue()
            .stream()
            .map(agencyMapper::toResponse)
            .toList();
    }

    public List<AgencyResponse> getAllAgencies() {
        if (scopeService.isSystemAdmin()) {
            return agencyRepository.findAll()
                .stream()
                .map(agencyMapper::toResponse)
                .toList();
        } else {
            UUID agencyId = scopeService.getCurrentPrincipal().getAgencyId();
            if (agencyId == null) {
                return List.of();
            }
            return agencyRepository.findById(agencyId)
                .stream()
                .map(agencyMapper::toResponse)
                .toList();
        }
    }

    public AgencyResponse getAgencyById(UUID id) {
        if (!scopeService.isSystemAdmin()) {
            scopeService.validateAgencyScope(id);
        }
        return agencyRepository.findById(id)
            .map(agencyMapper::toResponse)
            .orElseThrow(() -> new ResourceNotFoundException("Agency not found: " + id));
    }

    @Transactional
    public AgencyResponse createAgency(CreateAgencyRequest request) {
        UUID agencyId = UUID.randomUUID();
        AgencyEntity entity = new AgencyEntity(
            agencyId,
            request.name(),
            request.code(),
            request.type(),
            request.emergencyNumber(),
            true,
            false
        );
        agencyRepository.save(entity);

        UUID actorId = scopeService.getCurrentPrincipal().getId();
        auditService.logAction(actorId, "AGENCY_CREATED", "AGENCY", agencyId, agencyId, "Created agency " + entity.getName(), null, null);

        return agencyMapper.toResponse(entity);
    }

    @Transactional
    public AgencyResponse updateAgency(UUID id, UpdateAgencyRequest request) {
        scopeService.validateAgencyScope(id);

        AgencyEntity entity = agencyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Agency not found: " + id));

        entity.setName(request.name());
        entity.setType(request.type());
        entity.setEmergencyNumber(request.emergencyNumber());
        entity.setUpdatedAt(LocalDateTime.now());

        agencyRepository.save(entity);

        UUID actorId = scopeService.getCurrentPrincipal().getId();
        auditService.logAction(actorId, "AGENCY_UPDATED", "AGENCY", id, id, "Updated agency " + entity.getName(), null, null);

        return agencyMapper.toResponse(entity);
    }

    @Transactional
    public AgencyResponse verifyAgency(UUID id) {
        AgencyEntity entity = agencyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Agency not found: " + id));

        entity.setVerified(true);
        entity.setUpdatedAt(LocalDateTime.now());
        agencyRepository.save(entity);

        UUID actorId = scopeService.getCurrentPrincipal().getId();
        auditService.logAction(actorId, "AGENCY_VERIFIED", "AGENCY", id, id, "Verified agency " + entity.getName(), null, null);

        return agencyMapper.toResponse(entity);
    }

    @Transactional
    public AgencyResponse toggleAgencyStatus(UUID id) {
        scopeService.validateAgencyScope(id);

        AgencyEntity entity = agencyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Agency not found: " + id));

        entity.setActive(!entity.isActive());
        entity.setUpdatedAt(LocalDateTime.now());
        agencyRepository.save(entity);

        UUID actorId = scopeService.getCurrentPrincipal().getId();
        auditService.logAction(actorId, "AGENCY_STATUS_TOGGLED", "AGENCY", id, id, "Toggled active status to " + entity.isActive() + " for " + entity.getName(), null, null);

        return agencyMapper.toResponse(entity);
    }
}

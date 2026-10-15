package com.gec.backend.admin.service;

import com.gec.backend.admin.entity.AuditLogEntity;
import com.gec.backend.admin.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void logAction(UUID actorUserId, String action, String entityType, UUID entityId, UUID agencyId, String description, String ipAddress, String userAgent) {
        AuditLogEntity log = new AuditLogEntity(
            UUID.randomUUID(),
            actorUserId,
            action,
            entityType,
            entityId,
            agencyId,
            description,
            ipAddress,
            userAgent
        );
        auditLogRepository.save(log);
    }
}

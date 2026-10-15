package com.gec.backend.admin.repository;

import com.gec.backend.admin.entity.AdminRefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface AdminRefreshTokenRepository extends JpaRepository<AdminRefreshTokenEntity, UUID> {
    Optional<AdminRefreshTokenEntity> findByTokenHashAndRevokedAtIsNull(String tokenHash);
    void deleteByUserId(UUID userId);
}

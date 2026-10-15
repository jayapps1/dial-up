package com.gec.backend.admin.controller;

import com.gec.backend.admin.dto.ResetPasswordResponse;
import com.gec.backend.admin.dto.TemporaryCredentialResponse;
import com.gec.backend.admin.service.TemporaryCredentialsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/temporary-credentials")
public class TemporaryCredentialsController {

    private final TemporaryCredentialsService credentialsService;

    public TemporaryCredentialsController(TemporaryCredentialsService credentialsService) {
        this.credentialsService = credentialsService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN_USER_READ')")
    public ResponseEntity<List<TemporaryCredentialResponse>> getPendingCredentials() {
        return ResponseEntity.ok(credentialsService.getPendingCredentials());
    }

    @PostMapping("/users/{userId}/reset-password")
    @PreAuthorize("hasAuthority('ADMIN_USER_UPDATE')")
    public ResponseEntity<ResetPasswordResponse> resetTemporaryPassword(@PathVariable UUID userId) {
        return ResponseEntity.ok(credentialsService.resetTemporaryPassword(userId));
    }
}

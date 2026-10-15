package com.gec.backend.admin.controller;

import com.gec.backend.admin.dto.AdminUserResponse;
import com.gec.backend.admin.dto.ChangePasswordRequest;
import com.gec.backend.admin.dto.LoginRequest;
import com.gec.backend.admin.dto.LoginResponse;
import com.gec.backend.admin.security.AdminUserPrincipal;
import com.gec.backend.admin.service.AdminAuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/admin/auth")
public class AdminAuthController {

    private final AdminAuthService authService;

    public AdminAuthController(AdminAuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
        @Valid @RequestBody LoginRequest request,
        HttpServletRequest httpRequest
    ) {
        String ipAddress = httpRequest.getRemoteAddr();
        String userAgent = httpRequest.getHeader("User-Agent");

        LoginResponse response = authService.login(request, ipAddress, userAgent);
        String refreshToken = authService.createRefreshToken(response.user().id());

        ResponseCookie cookie = ResponseCookie.from("gec_refresh_token", refreshToken)
            .httpOnly(true)
            .secure(false)
            .path("/api/v1/admin/auth")
            .maxAge(7 * 24 * 60 * 60)
            .sameSite("Lax")
            .build();

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(HttpServletRequest httpRequest) {
        String refreshToken = null;
        if (httpRequest.getCookies() != null) {
            refreshToken = Arrays.stream(httpRequest.getCookies())
                .filter(c -> "gec_refresh_token".equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
        }

        String ipAddress = httpRequest.getRemoteAddr();
        String userAgent = httpRequest.getHeader("User-Agent");

        LoginResponse response = authService.refreshAccessToken(refreshToken, ipAddress, userAgent);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
        HttpServletRequest httpRequest,
        @AuthenticationPrincipal AdminUserPrincipal principal
    ) {
        String refreshToken = null;
        if (httpRequest.getCookies() != null) {
            refreshToken = Arrays.stream(httpRequest.getCookies())
                .filter(c -> "gec_refresh_token".equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
        }

        if (principal != null) {
            authService.logout(refreshToken, principal.getId());
        }

        ResponseCookie cookie = ResponseCookie.from("gec_refresh_token", "")
            .httpOnly(true)
            .path("/api/v1/admin/auth")
            .maxAge(0)
            .build();

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .build();
    }

    @GetMapping("/me")
    public ResponseEntity<AdminUserResponse> getCurrentUser(@AuthenticationPrincipal AdminUserPrincipal principal) {
        if (principal == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(new AdminUserResponse(
            principal.getId(),
            principal.getUsername(),
            principal.getFirstName(),
            principal.getLastName(),
            principal.getPhoneNumber(),
            principal.getAgencyId(),
            null,
            principal.getRegion(),
            principal.getStationId(),
            null,
            principal.isEnabled(),
            !principal.isAccountNonLocked(),
            principal.isMustChangePassword(),
            principal.getLastLoginAt(),
            Set.of()
        ));
    }

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(
        @Valid @RequestBody ChangePasswordRequest request,
        @AuthenticationPrincipal AdminUserPrincipal principal
    ) {
        if (principal == null) return ResponseEntity.status(401).build();
        authService.changePassword(principal.getId(), request);
        return ResponseEntity.ok().build();
    }
}

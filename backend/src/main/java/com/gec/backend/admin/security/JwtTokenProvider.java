package com.gec.backend.admin.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final long expirationMs;

    public JwtTokenProvider(
        @Value("${gec.jwt.secret:defaultSecretKeyForDevelopmentMustBeAtLeast32BytesLongForHS256!}") String secret,
        @Value("${gec.jwt.expiration-ms:900000}") long expirationMs
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    public String generateToken(AdminUserPrincipal principal) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", principal.getId().toString());
        if (principal.getAgencyId() != null) {
            claims.put("agencyId", principal.getAgencyId().toString());
        }
        if (principal.getRegion() != null) {
            claims.put("region", principal.getRegion());
        }
        if (principal.getStationId() != null) {
            claims.put("stationId", principal.getStationId().toString());
        }

        List<String> authorities = principal.getAuthorities().stream()
            .map(a -> a.getAuthority())
            .toList();
        claims.put("authorities", authorities);

        return Jwts.builder()
            .subject(principal.getUsername())
            .claims(claims)
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(secretKey)
            .compact();
    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();
        return claims.getSubject();
    }

    public UUID getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();
        String userIdStr = claims.get("userId", String.class);
        return userIdStr != null ? UUID.fromString(userIdStr) : null;
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public long getExpirationInSeconds() {
        return expirationMs / 1000;
    }
}

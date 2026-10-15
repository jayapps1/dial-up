package com.gec.backend.admin.security;

import com.gec.backend.admin.entity.AdminUserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.*;

public class AdminUserPrincipal implements UserDetails {

    private final UUID id;
    private final String email;
    private final String firstName;
    private final String lastName;
    private final String phoneNumber;
    private final String passwordHash;
    private final UUID agencyId;
    private final String region;
    private final UUID stationId;
    private final boolean active;
    private final boolean locked;
    private final boolean mustChangePassword;
    private final LocalDateTime lastLoginAt;
    private final Set<GrantedAuthority> authorities;

    public AdminUserPrincipal(AdminUserEntity user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.phoneNumber = user.getPhoneNumber();
        this.passwordHash = user.getPasswordHash();
        this.agencyId = user.getAgencyId();
        this.region = user.getRegion();
        this.stationId = user.getStationId();
        this.active = user.isActive();
        this.locked = user.isLocked();
        this.mustChangePassword = user.isMustChangePassword();
        this.lastLoginAt = user.getLastLoginAt();

        Set<GrantedAuthority> auths = new HashSet<>();
        user.getRoles().forEach(role -> {
            auths.add(new SimpleGrantedAuthority("ROLE_" + role.getCode()));
            role.getPermissions().forEach(perm -> {
                auths.add(new SimpleGrantedAuthority(perm.getCode()));
            });
        });
        this.authorities = Collections.unmodifiableSet(auths);
    }

    public UUID getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getPhoneNumber() { return phoneNumber; }
    public UUID getAgencyId() { return agencyId; }
    public String getRegion() { return region; }
    public UUID getStationId() { return stationId; }
    public boolean isMustChangePassword() { return mustChangePassword; }
    public LocalDateTime getLastLoginAt() { return lastLoginAt; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }

    @Override
    public String getPassword() { return passwordHash; }

    @Override
    public String getUsername() { return email; }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return !locked; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return active; }
}

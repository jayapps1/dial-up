package com.gec.backend.admin.service;

import com.gec.backend.admin.entity.AdminUserEntity;
import com.gec.backend.admin.entity.RoleEntity;
import com.gec.backend.admin.repository.AdminUserRepository;
import com.gec.backend.admin.repository.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class BootstrapAdminService implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(BootstrapAdminService.class);

    private final AdminUserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${gec.bootstrap.admin.email:nanagyachie@gmail.com}")
    private String bootstrapEmail;

    @Value("${gec.bootstrap.admin.password:Admin123!BootstrapSecret}")
    private String bootstrapPassword;

    @Value("${gec.bootstrap.admin.first-name:Nana}")
    private String bootstrapFirstName;

    @Value("${gec.bootstrap.admin.last-name:Gyachie}")
    private String bootstrapLastName;

    public BootstrapAdminService(
        AdminUserRepository userRepository,
        RoleRepository roleRepository,
        PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        String normalizedEmail = bootstrapEmail.trim().toLowerCase(Locale.US);
        Optional<AdminUserEntity> existingUserOpt = userRepository.findByEmailIgnoreCase(normalizedEmail);

        RoleEntity sysAdminRole = roleRepository.findByCode("SYSTEM_ADMIN")
            .orElseThrow(() -> new IllegalStateException("SYSTEM_ADMIN role missing in database."));

        if (existingUserOpt.isEmpty()) {
            String passwordToUse = (bootstrapPassword != null && !bootstrapPassword.isBlank())
                ? bootstrapPassword
                : "Admin123!BootstrapSecret";

            AdminUserEntity admin = new AdminUserEntity();
            admin.setId(UUID.randomUUID());
            admin.setEmail(normalizedEmail);
            admin.setPasswordHash(passwordEncoder.encode(passwordToUse));
            admin.setFirstName(bootstrapFirstName);
            admin.setLastName(bootstrapLastName);
            admin.setActive(true);
            admin.setMustChangePassword(false);
            admin.setRoles(Set.of(sysAdminRole));

            userRepository.save(admin);
            log.info("Bootstrap SYSTEM_ADMIN verified.");
        } else {
            AdminUserEntity admin = existingUserOpt.get();
            if (admin.getRoles().stream().noneMatch(r -> "SYSTEM_ADMIN".equals(r.getCode()))) {
                Set<RoleEntity> roles = new HashSet<>(admin.getRoles());
                roles.add(sysAdminRole);
                admin.setRoles(roles);
                userRepository.save(admin);
            }
            log.info("Bootstrap SYSTEM_ADMIN verified.");
        }
    }
}

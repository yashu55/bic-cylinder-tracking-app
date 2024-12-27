package com.bic.cylinder_tracking_api.config;

import com.bic.cylinder_tracking_api.entity.Role;
import com.bic.cylinder_tracking_api.entity.User;
import com.bic.cylinder_tracking_api.entity.enums.UserStatus;
import com.bic.cylinder_tracking_api.repository.RoleRepository;
import com.bic.cylinder_tracking_api.repository.UserRepository;
import com.bic.cylinder_tracking_api.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Configuration
@RequiredArgsConstructor
public class UserDataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(UserDataInitializer.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Override
    public void run(String... args) {

        // Ensure the ADMIN role exists
        Role adminRole = roleRepository.findByName("ADMIN").map(existingRole -> {
            logger.info("Admin role already exists");
            return existingRole;
        }).orElseGet(() -> {
            Role newRole = new Role();
            newRole.setName("ADMIN");
            newRole.setDescription("Administrator with full access");
            Role savedRole = roleRepository.save(newRole);
            logger.info("Admin role created");
            return savedRole;
        });

        // Ensure the ADMIN role exists
        Role userRole = roleRepository.findByName("USER").map(existingRole -> {
            logger.info("User role already exists");
            return existingRole;
        }).orElseGet(() -> {
            Role newRole = new Role();
            newRole.setName("USER");
            newRole.setDescription("User with transaction access");
            Role savedRole = roleRepository.save(newRole);
            logger.info("User role created");
            return savedRole;
        });

        // Check if ADMIN created in system already
        if (userRepository.findByEmail(adminEmail).isPresent()) {
            logger.info("Admin user already exists");
            return;
        }

        // Create new Admin
        User adminUser = new User();
        adminUser.setName("System Administrator");
        adminUser.setEmail(adminEmail);
        String randomPassword = PasswordUtil.generateSecureRandomPassword();
        adminUser.setPasswordHash(passwordEncoder.encode(randomPassword));
        adminUser.setStatus(UserStatus.ACTIVE);
        adminUser.getRoles().add(adminRole);
        OffsetDateTime timestamp = OffsetDateTime.now(ZoneOffset.UTC);
        adminUser.setCreatedAt(timestamp);
        adminUser.setUpdatedAt(timestamp);
        userRepository.save(adminUser);
        logger.info("Admin user created with email: {} and password: {}", adminEmail, randomPassword);
    }
}


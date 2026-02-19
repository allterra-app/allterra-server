package com.allterra.server.model.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.EnumSet;

/**
 * Seeds default admin user on startup.
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public final class AdminSeedConfiguration {

    @Bean
    CommandLineRunner seedAdminUser(
            final UserRepository userRepository,
            final PasswordEncoder passwordEncoder,
            @Value("${security.seed-admin.enabled:false}") final boolean enabled,
            @Value("${security.seed-admin.email:admin@allterra.local}") final String adminEmail,
            @Value("${security.seed-admin.password:}") final String adminPassword
    ) {
        return args -> {
            if (!enabled) {
                return;
            }

            if (adminPassword == null || adminPassword.isBlank()) {
                log.warn("Seed admin is enabled but password is empty, skipping admin creation");
                return;
            }

            if (userRepository.existsByEmailIgnoreCase(adminEmail)) {
                return;
            }

            var admin = User.builder()
                    .email(adminEmail)
                    .password(passwordEncoder.encode(adminPassword))
                    .roles(EnumSet.of(UserRole.ADMIN))
                    .subscriptionPlan(SubscriptionPlan.FREE)
                    .build();
            userRepository.save(admin);
            log.info("Seed admin user created: {}", adminEmail);
        };
    }
}

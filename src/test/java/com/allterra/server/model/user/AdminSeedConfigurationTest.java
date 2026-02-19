package com.allterra.server.model.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminSeedConfigurationTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void seedAdminShouldCreateAdminWhenEnabledAndMissing() throws Exception {
        var configuration = new AdminSeedConfiguration();
        when(userRepository.existsByEmailIgnoreCase("admin@allterra.local")).thenReturn(false);
        when(passwordEncoder.encode("secret")).thenReturn("encoded-secret");

        CommandLineRunner runner = configuration.seedAdminUser(
                userRepository,
                passwordEncoder,
                true,
                "admin@allterra.local",
                "secret"
        );

        runner.run();

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getEmail()).isEqualTo("admin@allterra.local");
        assertThat(captor.getValue().getPassword()).isEqualTo("encoded-secret");
        assertThat(captor.getValue().getRoles()).containsExactly(UserRole.ADMIN);
    }

    @Test
    void seedAdminShouldSkipWhenDisabled() throws Exception {
        var configuration = new AdminSeedConfiguration();

        CommandLineRunner runner = configuration.seedAdminUser(
                userRepository,
                passwordEncoder,
                false,
                "admin@allterra.local",
                "secret"
        );

        runner.run();

        verify(userRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void seedAdminShouldSkipWhenPasswordBlank() throws Exception {
        var configuration = new AdminSeedConfiguration();

        CommandLineRunner runner = configuration.seedAdminUser(
                userRepository,
                passwordEncoder,
                true,
                "admin@allterra.local",
                ""
        );

        runner.run();

        verify(userRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void seedAdminShouldSkipWhenUserAlreadyExists() throws Exception {
        var configuration = new AdminSeedConfiguration();
        when(userRepository.existsByEmailIgnoreCase("admin@allterra.local")).thenReturn(true);

        CommandLineRunner runner = configuration.seedAdminUser(
                userRepository,
                passwordEncoder,
                true,
                "admin@allterra.local",
                "secret"
        );

        runner.run();

        verify(userRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }
}

package com.allterra.server.authentication.service;

import com.allterra.server.authentication.model.RefreshToken;
import com.allterra.server.authentication.repository.RefreshTokenRepository;
import com.allterra.server.model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(refreshTokenService, "refreshTokenExpirationMs", 60_000L);
    }

    @Test
    void createRefreshTokenShouldPersistHashedToken() {
        var user = User.builder().id(com.allterra.server.TestUuids.id(1)).email("john@allterra.com").build();
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var rawToken = refreshTokenService.createRefreshToken(user);

        assertThat(rawToken).isNotBlank();
        var tokenCaptor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepository).save(tokenCaptor.capture());
        assertThat(tokenCaptor.getValue().getTokenHash()).isNotEqualTo(rawToken);
        assertThat(tokenCaptor.getValue().getTokenHash()).hasSize(64);
    }

    @Test
    void findValidTokenShouldReturnTokenWhenNotExpiredAndNotRevoked() {
        var token = RefreshToken.builder()
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .revokedAt(null)
                .build();
        when(refreshTokenRepository.findByTokenHash(any())).thenReturn(Optional.of(token));

        var result = refreshTokenService.findValidToken("raw-token");

        assertThat(result).contains(token);
    }

    @Test
    void revokeTokenShouldReturnFalseForMissingToken() {
        when(refreshTokenRepository.findByTokenHash(any())).thenReturn(Optional.empty());

        assertThat(refreshTokenService.revokeToken("missing")).isFalse();
    }
}

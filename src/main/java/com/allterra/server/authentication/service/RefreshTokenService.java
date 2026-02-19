package com.allterra.server.authentication.service;

import com.allterra.server.authentication.model.RefreshToken;
import com.allterra.server.authentication.repository.RefreshTokenRepository;
import com.allterra.server.model.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Optional;

/**
 * Refresh token service.
 */
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private static final int REFRESH_TOKEN_BYTES = 32;

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${security.jwt.refresh-expiration-ms:2592000000}")
    long refreshTokenExpirationMs;

    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * Creates and stores refresh token for user.
     *
     * @param user user for refresh token
     * @return raw refresh token value
     */
    @Transactional
    public String createRefreshToken(final User user) {
        var rawToken = generateRawToken();
        var now = LocalDateTime.now();
        var refreshToken = RefreshToken.builder()
                .user(user)
                .tokenHash(hashToken(rawToken))
                .createdAt(now)
                .expiresAt(now.plus(java.time.Duration.ofMillis(refreshTokenExpirationMs)))
                .build();
        refreshTokenRepository.save(refreshToken);
        return rawToken;
    }

    /**
     * Finds valid refresh token.
     *
     * @param rawToken raw token value
     * @return optional valid token
     */
    @Transactional(readOnly = true)
    public Optional<RefreshToken> findValidToken(final String rawToken) {
        var now = LocalDateTime.now();
        return refreshTokenRepository.findByTokenHash(hashToken(rawToken))
                .filter(token -> token.getRevokedAt() == null)
                .filter(token -> token.getExpiresAt().isAfter(now));
    }

    /**
     * Revokes refresh token by raw value.
     *
     * @param rawToken raw token value
     * @return true if token existed and was active
     */
    @Transactional
    public boolean revokeToken(final String rawToken) {
        var now = LocalDateTime.now();
        var existing = refreshTokenRepository.findByTokenHash(hashToken(rawToken));
        if (existing.isEmpty()) {
            return false;
        }

        var token = existing.get();
        if (token.getRevokedAt() != null) {
            return false;
        }

        token.setRevokedAt(now);
        refreshTokenRepository.save(token);
        return true;
    }

    /**
     * Revokes a refresh token instance.
     *
     * @param token token entity
     */
    @Transactional
    public void revoke(final RefreshToken token) {
        token.setRevokedAt(LocalDateTime.now());
        refreshTokenRepository.save(token);
    }

    private String generateRawToken() {
        var bytes = new byte[REFRESH_TOKEN_BYTES];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hashToken(final String rawToken) {
        try {
            var digest = MessageDigest.getInstance("SHA-256");
            var hash = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 is not available", ex);
        }
    }
}

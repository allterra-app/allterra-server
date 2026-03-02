package com.allterra.server.authentication;

import io.jsonwebtoken.Jwts;
import com.allterra.server.model.user.UserRole;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtTokenProviderTest {

    private static final String ISSUER = "allterra-server";
    private static final String AUDIENCE = "allterra-client";

    @Test
    void generateTokenShouldBeValidAndContainSubject() {
        var currentSecret = base64Secret((byte) 1);
        var provider = new JwtTokenProvider(currentSecret, "", 3_600_000, ISSUER, AUDIENCE, 30, "key-v1");

        var token = provider.generateToken("user@allterra.com");

        assertThat(provider.validateToken(token)).isEqualTo(JwtTokenProvider.ValidateTokenStatus.VALID);
        assertThat(provider.getUserEmailFromToken(token)).isEqualTo("user@allterra.com");
        assertThat(provider.getUserRolesFromToken(token)).containsExactly(UserRole.USER);
    }

    @Test
    void generateTokenShouldStoreCustomRoles() {
        var currentSecret = base64Secret((byte) 1);
        var provider = new JwtTokenProvider(currentSecret, "", 3_600_000, ISSUER, AUDIENCE, 30, "key-v1");

        var token = provider.generateToken("admin@allterra.com", Set.of(UserRole.ADMIN, UserRole.USER));

        assertThat(provider.getUserRolesFromToken(token)).containsExactlyInAnyOrder(UserRole.ADMIN, UserRole.USER);
    }

    @Test
    void validateTokenShouldAcceptTokenSignedWithPreviousSecret() {
        var oldSecret = base64Secret((byte) 2);
        var newSecret = base64Secret((byte) 3);
        var provider = new JwtTokenProvider(newSecret, oldSecret, 3_600_000, ISSUER, AUDIENCE, 30, "key-v2");

        var oldKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(oldSecret));
        var now = Instant.now();
        var token = Jwts.builder()
                .setSubject("legacy@allterra.com")
                .setIssuer(ISSUER)
                .setAudience(AUDIENCE)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(300)))
                .claim("token_type", "access")
                .signWith(oldKey)
                .compact();

        assertThat(provider.validateToken(token)).isEqualTo(JwtTokenProvider.ValidateTokenStatus.VALID);
        assertThat(provider.getUserEmailFromToken(token)).isEqualTo("legacy@allterra.com");
    }

    @Test
    void validateTokenShouldReturnExpiredForExpiredToken() {
        var secret = base64Secret((byte) 4);
        var provider = new JwtTokenProvider(secret, "", 3_600_000, ISSUER, AUDIENCE, 0, "key-v1");
        var key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
        var now = Instant.now();
        var token = Jwts.builder()
                .setSubject("user@allterra.com")
                .setIssuer(ISSUER)
                .setAudience(AUDIENCE)
                .setIssuedAt(Date.from(now.minusSeconds(120)))
                .setExpiration(Date.from(now.minusSeconds(60)))
                .claim("token_type", "access")
                .signWith(key)
                .compact();

        assertThat(provider.validateToken(token)).isEqualTo(JwtTokenProvider.ValidateTokenStatus.EXPIRED);
    }

    @Test
    void validateTokenShouldReturnInvalidSignatureForForeignSecret() {
        var currentSecret = base64Secret((byte) 5);
        var foreignSecret = base64Secret((byte) 6);
        var provider = new JwtTokenProvider(currentSecret, "", 3_600_000, ISSUER, AUDIENCE, 30, "key-v1");
        var foreignKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(foreignSecret));
        var now = Instant.now();
        var token = Jwts.builder()
                .setSubject("user@allterra.com")
                .setIssuer(ISSUER)
                .setAudience(AUDIENCE)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(300)))
                .claim("token_type", "access")
                .signWith(foreignKey)
                .compact();

        assertThat(provider.validateToken(token)).isEqualTo(JwtTokenProvider.ValidateTokenStatus.INVALID_SIGNATURE);
    }

    @Test
    void validateTokenShouldReturnInvalidForMalformedToken() {
        var currentSecret = base64Secret((byte) 7);
        var provider = new JwtTokenProvider(currentSecret, "", 3_600_000, ISSUER, AUDIENCE, 30, "key-v1");

        assertThat(provider.validateToken("malformed.jwt.token"))
                .isEqualTo(JwtTokenProvider.ValidateTokenStatus.INVALID);
    }

    @Test
    void validateTokenShouldRejectNoneAlgorithm() {
        var currentSecret = base64Secret((byte) 7);
        var provider = new JwtTokenProvider(currentSecret, "", 3_600_000, ISSUER, AUDIENCE, 30, "key-v1");

        var header = Base64.getUrlEncoder().withoutPadding()
                .encodeToString("{\"alg\":\"none\",\"typ\":\"JWT\"}".getBytes(java.nio.charset.StandardCharsets.UTF_8));
        var payload = Base64.getUrlEncoder().withoutPadding()
                .encodeToString("{\"sub\":\"user@allterra.com\"}".getBytes(java.nio.charset.StandardCharsets.UTF_8));
        var token = header + "." + payload + ".";

        assertThat(provider.validateToken(token))
                .isEqualTo(JwtTokenProvider.ValidateTokenStatus.UNSUPPORTED_ALGORITHM);
    }

    @Test
    void constructorShouldRejectInvalidBase64Secret() {
        assertThatThrownBy(() -> new JwtTokenProvider("###", "", 3_600_000, ISSUER, AUDIENCE, 30, "key-v1"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("valid Base64");
    }

    @Test
    void constructorShouldRejectShortSecret() {
        var shortSecret = Base64.getEncoder().encodeToString("too-short".getBytes());

        assertThatThrownBy(() -> new JwtTokenProvider(shortSecret, "", 3_600_000, ISSUER, AUDIENCE, 30, "key-v1"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("at least 64 bytes");
    }

    private static String base64Secret(final byte value) {
        var bytes = new byte[64];
        Arrays.fill(bytes, value);
        return Base64.getEncoder().encodeToString(bytes);
    }
}

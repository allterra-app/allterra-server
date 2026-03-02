package com.allterra.server.authentication;

import com.allterra.server.model.user.UserRole;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * JWT token provider.
 */
@Slf4j
@Component
public class JwtTokenProvider {

    private static final int HS512_MIN_KEY_LENGTH_BYTES = 64;
    private static final String TOKEN_TYPE_CLAIM = "token_type";
    private static final String ACCESS_TOKEN_TYPE = "access";
    private static final String ROLES_CLAIM = "roles";
    private static final String ALGORITHM_HEADER = "alg";
    private static final String DISALLOWED_NONE_ALGORITHM = "none";
    private static final TypeReference<Map<String, Object>> MAP_TYPE_REFERENCE = new TypeReference<>() { };

    private final Key signInKey;
    private final Key previousSignInKey;
    private final Duration jwtExpiration;
    private final String issuer;
    private final String audience;
    private final long clockSkewSeconds;
    private final String currentKeyId;
    private final ObjectMapper objectMapper;

    public JwtTokenProvider(
            @Value("${security.jwt.secret}") final String jwtSecret,
            @Value("${security.jwt.previous-secret:}") final String previousJwtSecret,
            @Value("${security.jwt.expiration-ms:900000}") final long jwtExpirationMs,
            @Value("${security.jwt.issuer:allterra-server}") final String issuer,
            @Value("${security.jwt.audience:allterra-client}") final String audience,
            @Value("${security.jwt.clock-skew-seconds:30}") final long clockSkewSeconds,
            @Value("${security.jwt.current-key-id:current}") final String currentKeyId
    ) {
        this.signInKey = parseSigningKey(jwtSecret);
        this.previousSignInKey = parseOptionalSigningKey(previousJwtSecret);
        this.jwtExpiration = Duration.ofMillis(jwtExpirationMs);
        this.issuer = issuer;
        this.audience = audience;
        this.clockSkewSeconds = clockSkewSeconds;
        this.currentKeyId = currentKeyId;
        this.objectMapper = new ObjectMapper();
    }

    @PostConstruct
    private void validateConfiguration() {
        if (jwtExpiration.isZero() || jwtExpiration.isNegative()) {
            throw new IllegalStateException("JWT expiration must be greater than 0");
        }
        if (clockSkewSeconds < 0) {
            throw new IllegalStateException("JWT clock skew must be >= 0");
        }
    }

    /**
     * Generate token.
     *
     * @param userEmail User email
     * @return generated token
     */
    public String generateToken(final String userEmail) {
        return generateToken(userEmail, Set.of(UserRole.USER));
    }

    /**
     * Generate token with roles.
     *
     * @param userEmail User email
     * @param roles user roles
     * @return generated token
     */
    public String generateToken(final String userEmail, final Set<UserRole> roles) {
        var now = Instant.now();
        var issuedAt = Date.from(now);
        var expiryDate = Date.from(now.plus(jwtExpiration));
        var safeRoles = (roles == null || roles.isEmpty())
                ? Set.of(UserRole.USER)
                : roles;

        return Jwts.builder()
                .setHeaderParam("kid", currentKeyId)
                .setId(java.util.UUID.randomUUID().toString())
                .setSubject(userEmail)
                .setIssuer(issuer)
                .setAudience(audience)
                .setIssuedAt(issuedAt)
                .setExpiration(expiryDate)
                .claim(TOKEN_TYPE_CLAIM, ACCESS_TOKEN_TYPE)
                .claim(ROLES_CLAIM, safeRoles.stream().map(Enum::name).toList())
                .signWith(signInKey, SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Get User email by token.
     *
     * @param token token
     * @return User email
     */
    public String getUserEmailFromToken(final String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * Get user roles by token.
     *
     * @param token token
     * @return set of user roles
     */
    public Set<UserRole> getUserRolesFromToken(final String token) {
        var claims = parseClaims(token);
        List<?> rolesFromClaims = claims.get(ROLES_CLAIM, List.class);
        if (rolesFromClaims == null || rolesFromClaims.isEmpty()) {
            return Set.of(UserRole.USER);
        }

        var parsedRoles = rolesFromClaims.stream()
                .filter(java.util.Objects::nonNull)
                .map(String::valueOf)
                .map(role -> {
                    try {
                        return UserRole.valueOf(role);
                    } catch (IllegalArgumentException ex) {
                        return null;
                    }
                })
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(UserRole.class)));
        return parsedRoles.isEmpty() ? Set.of(UserRole.USER) : parsedRoles;
    }

    /**
     * Validate token.
     *
     * @param token token that must be validated
     * @return validation result
     */
    public ValidateTokenStatus validateToken(final String token) {
        try {
            parseClaims(token);
            return ValidateTokenStatus.VALID;
        } catch (ExpiredJwtException ex) {
            log.warn("JWT is expired");
            return ValidateTokenStatus.EXPIRED;
        } catch (SecurityException ex) {
            log.warn("JWT signature validation failed");
            return ValidateTokenStatus.INVALID_SIGNATURE;
        } catch (UnsupportedJwtException ex) {
            log.warn("JWT algorithm is unsupported");
            return ValidateTokenStatus.UNSUPPORTED_ALGORITHM;
        } catch (MalformedJwtException | IllegalArgumentException ex) {
            log.warn("JWT is malformed or unsupported");
            return ValidateTokenStatus.INVALID;
        }
    }

    private Claims parseClaims(final String token) {
        validateTokenHeader(token);
        try {
            return parseClaimsWithKey(token, signInKey);
        } catch (SecurityException ex) {
            if (previousSignInKey == null) {
                throw ex;
            }
            return parseClaimsWithKey(token, previousSignInKey);
        }
    }

    private Claims parseClaimsWithKey(final String token, final Key key) {
        var parserBuilder = Jwts.parserBuilder()
            .setSigningKey(key)
            .setAllowedClockSkewSeconds(clockSkewSeconds)
            .require(TOKEN_TYPE_CLAIM, ACCESS_TOKEN_TYPE);

        if (issuer != null && !issuer.isBlank()) {
            parserBuilder.requireIssuer(issuer);
        }
        if (audience != null && !audience.isBlank()) {
            parserBuilder.requireAudience(audience);
        }

        var parsedClaims = parserBuilder
            .build()
            .parseClaimsJws(token)
            .getBody();
        return parsedClaims;
    }

    private void validateTokenHeader(final String token) {
        final var algorithm = extractTokenAlgorithm(token);
        if (algorithm == null || algorithm.isBlank()) {
            throw new UnsupportedJwtException("JWT algorithm header is missing");
        }
        if (DISALLOWED_NONE_ALGORITHM.equalsIgnoreCase(algorithm)) {
            throw new UnsupportedJwtException("JWT with 'alg=none' is not allowed");
        }
        if (!SignatureAlgorithm.HS512.getValue().equalsIgnoreCase(algorithm)) {
            throw new UnsupportedJwtException("Unsupported JWT algorithm: " + algorithm);
        }
    }

    private String extractTokenAlgorithm(final String token) {
        try {
            final var dotIndex = token.indexOf('.');
            if (dotIndex <= 0) {
                throw new MalformedJwtException("JWT header segment is missing");
            }
            final var encodedHeader = token.substring(0, dotIndex);
            final var decoded = Decoders.BASE64URL.decode(encodedHeader);
            final var headerMap = objectMapper.readValue(decoded, MAP_TYPE_REFERENCE);
            return Objects.toString(headerMap.get(ALGORITHM_HEADER), null);
        } catch (IllegalArgumentException exception) {
            throw new MalformedJwtException("JWT header is malformed", exception);
        } catch (java.io.IOException exception) {
            throw new MalformedJwtException("JWT header JSON is malformed", exception);
        }
    }

    private Key parseSigningKey(final String jwtSecret) {
        byte[] keyBytes;
        try {
            keyBytes = Decoders.BASE64.decode(jwtSecret);
        } catch (RuntimeException ex) {
            throw new IllegalStateException("JWT secret must be valid Base64", ex);
        }

        if (keyBytes.length < HS512_MIN_KEY_LENGTH_BYTES) {
            throw new IllegalStateException("JWT secret must be at least 64 bytes after Base64 decoding for HS512");
        }

        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Key parseOptionalSigningKey(final String jwtSecret) {
        if (jwtSecret == null || jwtSecret.isBlank()) {
            return null;
        }
        return parseSigningKey(jwtSecret);
    }

    /**
     * Validate status of the token.
     */
    public enum ValidateTokenStatus {
        VALID(true),
        EXPIRED(false),
        INVALID_SIGNATURE(false),
        UNSUPPORTED_ALGORITHM(false),
        INVALID(false);

        public final boolean isValid;

        ValidateTokenStatus(final boolean isValid) {
            this.isValid = isValid;
        }
    }
}

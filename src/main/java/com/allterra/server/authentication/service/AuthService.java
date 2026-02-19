package com.allterra.server.authentication.service;

import com.allterra.server.authentication.JwtTokenProvider;
import com.allterra.server.authentication.dto.JwtAuthenticationRequestDto;
import com.allterra.server.authentication.dto.JwtAuthenticationResponseDto;
import com.allterra.server.authentication.exception.InvalidCredentialsException;
import com.allterra.server.authentication.exception.InvalidRefreshTokenException;
import com.allterra.server.model.user.User;
import com.allterra.server.model.user.UserRole;
import com.allterra.server.model.user.UserRepository;
import com.allterra.server.model.user.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

/**
 * Service for User Authentication.
 */
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthService {

    UserRepository userRepository;

    UserService userService;

    PasswordEncoder passwordEncoder;

    JwtTokenProvider jwtTokenProvider;

    RefreshTokenService refreshTokenService;

    /**
     * Register User.
     *
     * @param request request for registration
     * @return generated token for registered User
     */
    public JwtAuthenticationResponseDto registerUser(final JwtAuthenticationRequestDto request) {
        var userResponseDto = userService.registerUser(request);
        var roles = userResponseDto.getRoles() == null || userResponseDto.getRoles().isEmpty()
                ? Set.of(UserRole.USER)
                : userResponseDto.getRoles();
        var accessToken = jwtTokenProvider.generateToken(userResponseDto.getEmail(), roles);
        var user = findByEmail(userResponseDto.getEmail())
                .orElseThrow(() -> new IllegalStateException("Registered user not found"));
        var refreshToken = refreshTokenService.createRefreshToken(user);
        return new JwtAuthenticationResponseDto(accessToken, refreshToken);
    }

    /**
     * Authenticate User.
     *
     * @param request request for authentication
     * @return token for authenticated User
     */
    public JwtAuthenticationResponseDto authenticateUser(final JwtAuthenticationRequestDto request) {
        var user = findByEmail(request.email())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        var roles = user.getRoles() == null || user.getRoles().isEmpty()
                ? Set.of(UserRole.USER)
                : user.getRoles();
        var accessToken = jwtTokenProvider.generateToken(user.getEmail(), roles);
        var refreshToken = refreshTokenService.createRefreshToken(user);
        return new JwtAuthenticationResponseDto(accessToken, refreshToken);
    }

    /**
     * Refreshes authentication tokens by refresh token.
     *
     * @param refreshToken raw refresh token
     * @return new access and refresh tokens
     */
    public JwtAuthenticationResponseDto refreshTokens(final String refreshToken) {
        var token = refreshTokenService.findValidToken(refreshToken)
                .orElseThrow(() -> new InvalidRefreshTokenException("Refresh token is invalid or expired"));
        var user = token.getUser();
        var roles = user.getRoles() == null || user.getRoles().isEmpty()
                ? Set.of(UserRole.USER)
                : user.getRoles();

        refreshTokenService.revoke(token);
        var newAccessToken = jwtTokenProvider.generateToken(user.getEmail(), roles);
        var newRefreshToken = refreshTokenService.createRefreshToken(user);
        return new JwtAuthenticationResponseDto(newAccessToken, newRefreshToken);
    }

    /**
     * Revokes refresh token.
     *
     * @param refreshToken raw refresh token
     */
    public void logout(final String refreshToken) {
        refreshTokenService.revokeToken(refreshToken);
    }

    private Optional<User> findByEmail(final String email) {
        return userRepository.findByEmailIgnoreCase(email);
    }
}

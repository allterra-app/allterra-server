package com.allterra.server.authentication.controller;

import com.allterra.server.authentication.dto.JwtAuthenticationRequestDto;
import com.allterra.server.authentication.dto.JwtAuthenticationResponseDto;
import com.allterra.server.authentication.dto.RefreshTokenRequestDto;
import com.allterra.server.authentication.service.AuthService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for User Authentication.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {

    AuthService authService;

    /**
     * Register new User.
     *
     * @param request request for registration
     * @return {@link ResponseEntity for register user}
     */
    @PostMapping("/register")
    public ResponseEntity<JwtAuthenticationResponseDto> registerUser(
            final @RequestBody @Valid JwtAuthenticationRequestDto request
    ) {
        return ResponseEntity.ok(authService.registerUser(request));
    }

    /**
     * Authenticate User.
     *
     * @param request request for authentication.
     *
     * @return {@link ResponseEntity} for authenticated user
     */
    @PostMapping("/login")
    public ResponseEntity<JwtAuthenticationResponseDto> authenticateUser(
            final @RequestBody @Valid JwtAuthenticationRequestDto request
    ) {
        return ResponseEntity.ok(authService.authenticateUser(request));
    }

    /**
     * Refreshes access token and rotates refresh token.
     *
     * @param request refresh token request
     * @return new token pair
     */
    @PostMapping("/refresh")
    public ResponseEntity<JwtAuthenticationResponseDto> refresh(final @RequestBody @Valid RefreshTokenRequestDto request) {
        return ResponseEntity.ok(authService.refreshTokens(request.refreshToken()));
    }

    /**
     * Logout by revoking refresh token.
     *
     * @param request refresh token request
     * @return empty response
     */
    @DeleteMapping("/logout")
    public ResponseEntity<Void> logout(final @RequestBody @Valid RefreshTokenRequestDto request) {
        authService.logout(request.refreshToken());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

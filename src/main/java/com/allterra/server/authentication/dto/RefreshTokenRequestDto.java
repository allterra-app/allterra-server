package com.allterra.server.authentication.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Request DTO for refresh token operations.
 *
 * @param refreshToken refresh token string
 */
public record RefreshTokenRequestDto(@NotBlank(message = "Refresh token is required") String refreshToken) {}

package com.allterra.server.authentication.dto;

/**
 * DTO model for Authentication response.
 *
 * @param accessToken generated access token
 * @param refreshToken generated refresh token
 */
public record JwtAuthenticationResponseDto(String accessToken, String refreshToken) {}

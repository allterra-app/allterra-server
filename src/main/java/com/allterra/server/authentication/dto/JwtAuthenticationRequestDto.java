package com.allterra.server.authentication.dto;

/**
 * DTO model for Authentication request.
 *
 * @param email user email
 * @param password user password
 */
public record JwtAuthenticationRequestDto(String email, String password) {}

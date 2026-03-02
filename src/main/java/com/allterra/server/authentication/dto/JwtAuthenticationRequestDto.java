package com.allterra.server.authentication.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO model for Authentication request.
 *
 * @param email user email
 * @param password user password
 */
public record JwtAuthenticationRequestDto(
        @NotBlank(message = "Email is required")
        @Email(message = "Email is invalid")
        String email,

        @NotBlank(message = "Password is required")
        String password
) { }

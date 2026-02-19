package com.allterra.server.authentication.controller;

import com.allterra.server.authentication.dto.JwtAuthenticationRequestDto;
import com.allterra.server.authentication.dto.JwtAuthenticationResponseDto;
import com.allterra.server.authentication.dto.RefreshTokenRequestDto;
import com.allterra.server.authentication.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    void registerUserShouldReturnTokenInResponseBody() {
        var request = new JwtAuthenticationRequestDto("john@allterra.com", "secret");
        when(authService.registerUser(request)).thenReturn(new JwtAuthenticationResponseDto("jwt-register", "refresh"));

        ResponseEntity<JwtAuthenticationResponseDto> response = authController.registerUser(request);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(new JwtAuthenticationResponseDto("jwt-register", "refresh"));
    }

    @Test
    void authenticateUserShouldReturnTokenInResponseBody() {
        var request = new JwtAuthenticationRequestDto("john@allterra.com", "secret");
        when(authService.authenticateUser(request)).thenReturn(new JwtAuthenticationResponseDto("jwt-login", "refresh"));

        ResponseEntity<JwtAuthenticationResponseDto> response = authController.authenticateUser(request);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(new JwtAuthenticationResponseDto("jwt-login", "refresh"));
    }

    @Test
    void refreshShouldReturnTokenPair() {
        var request = new RefreshTokenRequestDto("refresh-token");
        when(authService.refreshTokens("refresh-token")).thenReturn(new JwtAuthenticationResponseDto("jwt-new", "refresh-new"));

        ResponseEntity<JwtAuthenticationResponseDto> response = authController.refresh(request);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(new JwtAuthenticationResponseDto("jwt-new", "refresh-new"));
    }

    @Test
    void logoutShouldReturnNoContent() {
        var request = new RefreshTokenRequestDto("refresh-token");
        doNothing().when(authService).logout("refresh-token");

        ResponseEntity<Void> response = authController.logout(request);

        assertThat(response.getStatusCode().value()).isEqualTo(204);
    }
}

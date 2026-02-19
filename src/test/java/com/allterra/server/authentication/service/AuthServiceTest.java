package com.allterra.server.authentication.service;

import com.allterra.server.authentication.JwtTokenProvider;
import com.allterra.server.authentication.dto.JwtAuthenticationRequestDto;
import com.allterra.server.authentication.dto.JwtAuthenticationResponseDto;
import com.allterra.server.authentication.exception.InvalidCredentialsException;
import com.allterra.server.authentication.model.RefreshToken;
import com.allterra.server.model.user.User;
import com.allterra.server.model.user.UserRole;
import com.allterra.server.model.user.UserRepository;
import com.allterra.server.model.user.UserService;
import com.allterra.server.model.user.dto.UserResponseDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserService userService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private AuthService authService;

    @Test
    void registerUserShouldReturnTokenPair() {
        var request = new JwtAuthenticationRequestDto("john@allterra.com", "secret");
        var userResponse = UserResponseDto.builder()
                .email("john@allterra.com")
                .roles(Set.of(UserRole.USER))
                .build();
        var user = User.builder()
                .email("john@allterra.com")
                .roles(Set.of(UserRole.USER))
                .build();

        when(userService.registerUser(request)).thenReturn(userResponse);
        when(userRepository.findByEmailIgnoreCase("john@allterra.com"))
                .thenReturn(java.util.Optional.of(user));
        when(jwtTokenProvider.generateToken("john@allterra.com", Set.of(UserRole.USER))).thenReturn("access-token");
        when(refreshTokenService.createRefreshToken(user)).thenReturn("refresh-token");

        var tokenPair = authService.registerUser(request);

        assertThat(tokenPair).isEqualTo(new JwtAuthenticationResponseDto("access-token", "refresh-token"));
    }

    @Test
    void authenticateUserShouldReturnTokenPairWhenCredentialsAreValid() {
        var request = new JwtAuthenticationRequestDto("john@allterra.com", "secret");
        var user = User.builder().email("john@allterra.com").password("encoded").roles(Set.of(UserRole.ADMIN)).build();

        when(userRepository.findByEmailIgnoreCase("john@allterra.com")).thenReturn(java.util.Optional.of(user));
        when(passwordEncoder.matches("secret", "encoded")).thenReturn(true);
        when(jwtTokenProvider.generateToken("john@allterra.com", Set.of(UserRole.ADMIN))).thenReturn("access-token");
        when(refreshTokenService.createRefreshToken(user)).thenReturn("refresh-token");

        var tokenPair = authService.authenticateUser(request);

        assertThat(tokenPair).isEqualTo(new JwtAuthenticationResponseDto("access-token", "refresh-token"));
    }

    @Test
    void authenticateUserShouldThrowWhenUserNotFound() {
        var request = new JwtAuthenticationRequestDto("missing@allterra.com", "secret");
        when(userRepository.findByEmailIgnoreCase("missing@allterra.com")).thenReturn(java.util.Optional.empty());

        assertThatThrownBy(() -> authService.authenticateUser(request))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Invalid email or password");
    }

    @Test
    void authenticateUserShouldThrowWhenPasswordIsInvalid() {
        var request = new JwtAuthenticationRequestDto("john@allterra.com", "wrong");
        var user = User.builder().email("john@allterra.com").password("encoded").build();

        when(userRepository.findByEmailIgnoreCase("john@allterra.com")).thenReturn(java.util.Optional.of(user));
        when(passwordEncoder.matches("wrong", "encoded")).thenReturn(false);

        assertThatThrownBy(() -> authService.authenticateUser(request))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Invalid email or password");
    }

    @Test
    void refreshTokensShouldRotateRefreshToken() {
        var user = User.builder().email("john@allterra.com").roles(Set.of(UserRole.USER)).build();
        var refreshToken = RefreshToken.builder().user(user).build();

        when(refreshTokenService.findValidToken("old-refresh")).thenReturn(java.util.Optional.of(refreshToken));
        when(jwtTokenProvider.generateToken("john@allterra.com", Set.of(UserRole.USER))).thenReturn("new-access");
        when(refreshTokenService.createRefreshToken(user)).thenReturn("new-refresh");

        var tokenPair = authService.refreshTokens("old-refresh");

        assertThat(tokenPair).isEqualTo(new JwtAuthenticationResponseDto("new-access", "new-refresh"));
    }
}

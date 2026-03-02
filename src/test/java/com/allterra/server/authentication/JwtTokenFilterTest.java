package com.allterra.server.authentication;

import com.allterra.server.model.user.UserRole;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtTokenFilterTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    private JwtTokenFilter jwtTokenFilter;

    @BeforeEach
    void setUp() {
        var publicEndpointsMatcher = new OrRequestMatcher(
                new AntPathRequestMatcher("/auth/login", "POST"),
                new AntPathRequestMatcher("/auth/register", "POST"),
                new AntPathRequestMatcher("/auth/refresh", "POST")
        );
        jwtTokenFilter = new JwtTokenFilter(jwtTokenProvider, publicEndpointsMatcher);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterShouldSetAuthenticationWhenTokenIsValid() throws Exception {
        var request = new MockHttpServletRequest();
        request.setRequestURI("/api/points");
        request.addHeader("Authorization", "Bearer valid-token");
        var response = new MockHttpServletResponse();
        var filterChain = new MockFilterChain();

        when(jwtTokenProvider.validateToken("valid-token")).thenReturn(JwtTokenProvider.ValidateTokenStatus.VALID);
        when(jwtTokenProvider.getUserEmailFromToken("valid-token")).thenReturn("user@allterra.com");
        when(jwtTokenProvider.getUserRolesFromToken("valid-token")).thenReturn(java.util.Set.of(UserRole.USER));

        jwtTokenFilter.doFilter(request, response, filterChain);

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNotNull();
        assertThat(authentication.getName()).isEqualTo("user@allterra.com");
        assertThat(authentication.getAuthorities()).extracting("authority").containsExactly("ROLE_USER");
        verify(jwtTokenProvider).getUserEmailFromToken("valid-token");
    }

    @Test
    void doFilterShouldNotSetAuthenticationWhenTokenIsInvalid() throws Exception {
        var request = new MockHttpServletRequest();
        request.setRequestURI("/api/points");
        request.addHeader("Authorization", "Bearer invalid-token");
        var response = new MockHttpServletResponse();
        var filterChain = new MockFilterChain();

        when(jwtTokenProvider.validateToken("invalid-token")).thenReturn(JwtTokenProvider.ValidateTokenStatus.INVALID);

        jwtTokenFilter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(jwtTokenProvider, never()).getUserEmailFromToken("invalid-token");
    }

    @Test
    void doFilterShouldIgnoreNonBearerAuthorizationHeader() throws Exception {
        var request = new MockHttpServletRequest();
        request.setRequestURI("/api/points");
        request.addHeader("Authorization", "Basic credentials");
        var response = new MockHttpServletResponse();
        var filterChain = new MockFilterChain();

        jwtTokenFilter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(jwtTokenProvider, never()).validateToken(org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    void doFilterShouldSkipPublicAuthEndpoint() throws Exception {
        var request = new MockHttpServletRequest("POST", "/auth/login");
        request.setServletPath("/auth/login");
        var response = new MockHttpServletResponse();
        var filterChain = new MockFilterChain();

        jwtTokenFilter.doFilter(request, response, filterChain);

        verify(jwtTokenProvider, never()).validateToken(org.mockito.ArgumentMatchers.anyString());
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
}

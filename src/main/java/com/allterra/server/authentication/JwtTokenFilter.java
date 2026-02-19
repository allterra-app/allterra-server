package com.allterra.server.authentication;

import com.allterra.server.model.user.UserRole;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * JWT filter.
 */
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtTokenFilter extends OncePerRequestFilter {

    private static final String TOKEN_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";
    private static final int TOKEN_BEGIN_INDEX = 7;

    JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(
            final @NonNull HttpServletRequest request,
            final @NonNull HttpServletResponse response,
            final @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        if (request.getServletPath().startsWith("/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }

        var token = resolveToken(request);

        if (StringUtils.isNotBlank(token)) {
            var validationStatus = jwtTokenProvider.validateToken(token);
            if (validationStatus.isValid) {
                var username = jwtTokenProvider.getUserEmailFromToken(token);
                var authorities = mapRolesToAuthorities(jwtTokenProvider.getUserRolesFromToken(token));
                var authentication = new UsernamePasswordAuthenticationToken(username, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                request.setAttribute("auth_error_message", mapAuthErrorMessage(validationStatus));
            }
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(final HttpServletRequest request) {
        var bearerToken = request.getHeader(TOKEN_HEADER);
        if (StringUtils.isNotBlank(bearerToken) && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(TOKEN_BEGIN_INDEX);
        }

        return StringUtils.EMPTY;
    }

    private Set<GrantedAuthority> mapRolesToAuthorities(final Set<UserRole> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toSet());
    }

    private String mapAuthErrorMessage(final JwtTokenProvider.ValidateTokenStatus status) {
        return switch (status) {
            case EXPIRED -> "Access token is expired.";
            case INVALID_SIGNATURE -> "Access token signature is invalid.";
            case INVALID -> "Access token is invalid.";
            case VALID -> "Authentication token is valid.";
        };
    }
}

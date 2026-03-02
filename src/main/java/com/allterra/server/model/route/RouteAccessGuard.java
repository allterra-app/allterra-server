package com.allterra.server.model.route;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * Guard for route ownership checks in secured endpoints.
 */
@Component("routeAccessGuard")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RouteAccessGuard {

    RouteRepository routeRepository;

    /**
     * Checks if authenticated principal can access target route record.
     *
     * @param routeId target route id
     * @param authentication current authentication
     * @return {@code true} when access should be granted
     */
    public boolean canAccessRouteById(final java.util.UUID routeId, final Authentication authentication) {
        if (routeId == null || authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        final boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(authority -> authority.toUpperCase(Locale.ROOT))
                .anyMatch("ROLE_ADMIN"::equals);
        if (isAdmin) {
            return true;
        }

        final String principalEmail = authentication.getName();
        return principalEmail != null && routeRepository.existsByIdAndUser_EmailIgnoreCase(routeId, principalEmail);
    }
}

package com.allterra.server.model.poi;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * Guard for poi ownership checks in secured endpoints.
 */
@Component("poiAccessGuard")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PoiAccessGuard {

    PoiRepository poiRepository;

    /**
     * Checks if authenticated principal can access target poi record.
     *
     * @param poiId target poi id
     * @param authentication current authentication
     * @return {@code true} when access should be granted
     */
    public boolean canAccessPoiById(final java.util.UUID poiId, final Authentication authentication) {
        if (poiId == null || authentication == null || !authentication.isAuthenticated()) {
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
        return principalEmail != null && poiRepository.existsByIdAndUsers_EmailIgnoreCase(poiId, principalEmail);
    }
}

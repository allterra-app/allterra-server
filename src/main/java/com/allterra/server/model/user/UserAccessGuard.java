package com.allterra.server.model.user;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * Guard for user ownership checks in secured endpoints.
 */
@Component("userAccessGuard")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserAccessGuard {

    UserRepository userRepository;

    /**
     * Checks if authenticated principal can access target user record.
     *
     * @param userId target user id
     * @param authentication current authentication
     * @return {@code true} when access should be granted
     */
    public boolean canAccessUserById(final java.util.UUID userId, final Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(authority -> authority.toUpperCase(Locale.ROOT))
                .anyMatch("ROLE_ADMIN"::equals);
        if (isAdmin) {
            return true;
        }

        final String principalEmail = authentication.getName();
        return userRepository.findById(userId)
                .map(User::getEmail)
                .filter(email -> email != null && principalEmail != null)
                .map(email -> email.equalsIgnoreCase(principalEmail))
                .orElse(false);
    }
}

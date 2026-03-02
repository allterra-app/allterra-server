package com.allterra.server.model.post;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * Guard for post ownership checks in secured endpoints.
 */
@Component("postAccessGuard")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostAccessGuard {

    PostRepository postRepository;

    /**
     * Checks if authenticated principal can access target post record.
     *
     * @param postId target post id
     * @param authentication current authentication
     * @return {@code true} when access should be granted
     */
    public boolean canAccessPostById(final java.util.UUID postId, final Authentication authentication) {
        if (postId == null || authentication == null || !authentication.isAuthenticated()) {
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
        return principalEmail != null && postRepository.existsByIdAndUser_EmailIgnoreCase(postId, principalEmail);
    }
}

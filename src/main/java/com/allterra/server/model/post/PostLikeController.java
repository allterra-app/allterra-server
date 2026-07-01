package com.allterra.server.model.post;

import com.allterra.server.model.user.User;
import com.allterra.server.model.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Controller for post likes.
 */
@Slf4j
@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
@SuppressWarnings("checkstyle:DesignForExtension")
public class PostLikeController {
    private final PostLikeService postLikeService;
    private final UserRepository userRepository;

    /**
     * Likes a post.
     *
     * @param postId         post id
     * @param authentication auth
     * @return no content
     */
    @PutMapping("/{postId}/like")
    public ResponseEntity<Void> like(
            @PathVariable final UUID postId,
            final Authentication authentication
    ) {
        final var userId = resolveUserId(authentication);
        log.info("Like post {} by user {}", postId, userId);
        postLikeService.like(userId, postId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Unlikes a post.
     *
     * @param postId         post id
     * @param authentication auth
     * @return no content
     */
    @DeleteMapping("/{postId}/like")
    public ResponseEntity<Void> unlike(
            @PathVariable final UUID postId,
            final Authentication authentication
    ) {
        final var userId = resolveUserId(authentication);
        log.info("Unlike post {} by user {}", postId, userId);
        postLikeService.unlike(userId, postId);
        return ResponseEntity.noContent().build();
    }

    private UUID resolveUserId(final Authentication authentication) {
        final var email = authentication != null ? authentication.getName() : null;
        if (email == null) {
            throw new org.springframework.security.access.AccessDeniedException("Authentication required");
        }
        final var user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new org.springframework.security.access.AccessDeniedException("User not found"));
        return user.getId();
    }
}

package com.allterra.server.model.post;

import com.allterra.server.exception.ResourceNotFoundException;
import com.allterra.server.model.user.User;
import com.allterra.server.model.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for post likes.
 */
@Service
@RequiredArgsConstructor
@SuppressWarnings("checkstyle:DesignForExtension")
public class PostLikeService {
    private final JdbcTemplate jdbcTemplate;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    /**
     * Likes a post.
     *
     * @param userId user id
     * @param postId post id
     */
    @Transactional
    public void like(final UUID userId, final UUID postId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found: " + userId);
        }
        if (!postRepository.existsById(postId)) {
            throw new ResourceNotFoundException("Post not found: " + postId);
        }
        jdbcTemplate.update(
                "INSERT INTO post_likes (user_id, post_id) VALUES (?, ?) ON CONFLICT DO NOTHING",
                userId, postId
        );
    }

    /**
     * Unlikes a post.
     *
     * @param userId user id
     * @param postId post id
     */
    @Transactional
    public void unlike(final UUID userId, final UUID postId) {
        jdbcTemplate.update(
                "DELETE FROM post_likes WHERE user_id = ? AND post_id = ?",
                userId, postId
        );
    }

    /**
     * Returns like count for a single post.
     *
     * @param postId post id
     * @return like count
     */
    public int getLikeCount(final UUID postId) {
        final var result = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM post_likes WHERE post_id = ?",
                Integer.class, postId
        );
        return result != null ? result : 0;
    }

    /**
     * Returns whether a user liked a post.
     *
     * @param userId user id
     * @param postId post id
     * @return true if liked
     */
    public boolean isLikedBy(final UUID userId, final UUID postId) {
        if (userId == null) {
            return false;
        }
        final var result = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM post_likes WHERE user_id = ? AND post_id = ?",
                Integer.class, userId, postId
        );
        return result != null && result > 0;
    }

    /**
     * Returns like counts for a batch of post ids.
     *
     * @param postIds set of post ids
     * @return map of postId -> likeCount
     */
    public Map<UUID, Integer> getLikeCounts(final Set<UUID> postIds) {
        if (postIds == null || postIds.isEmpty()) {
            return Map.of();
        }
        final var rows = jdbcTemplate.queryForList(
                "SELECT post_id, COUNT(*) as cnt FROM post_likes WHERE post_id IN ("
                        + postIds.stream().map(id -> "'" + id + "'").collect(Collectors.joining(","))
                        + ") GROUP BY post_id"
        );
        return rows.stream()
                .collect(Collectors.toMap(
                        row -> (UUID) row.get("post_id"),
                        row -> ((Number) row.get("cnt")).intValue()
                ));
    }

    /**
     * Returns which posts are liked by a user from a batch.
     *
     * @param userId  user id
     * @param postIds set of post ids
     * @return set of post ids liked by this user
     */
    public Set<UUID> getLikedByUser(final UUID userId, final Set<UUID> postIds) {
        if (userId == null || postIds == null || postIds.isEmpty()) {
            return Set.of();
        }
        final var rows = jdbcTemplate.queryForList(
                "SELECT post_id FROM post_likes WHERE user_id = ? AND post_id IN ("
                        + postIds.stream().map(id -> "'" + id + "'").collect(Collectors.joining(","))
                        + ")",
                userId
        );
        return rows.stream()
                .map(row -> (UUID) row.get("post_id"))
                .collect(Collectors.toSet());
    }
}

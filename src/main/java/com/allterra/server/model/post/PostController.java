package com.allterra.server.model.post;

import com.allterra.server.model.post.dto.PostResponseDto;
import com.allterra.server.model.post.dto.request.PostCreateRequestDto;
import com.allterra.server.model.post.dto.request.PostUpdateRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static java.util.Objects.nonNull;

/**
 * Controller for {@link Post}.
 */
@Slf4j
@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    /**
     * Creates post for the User.
     *
     * @param userId id of user
     * @param post created post
     * @return {@link ResponseEntity} for created post
     */
    @PostMapping("/users/{userId}")
    public ResponseEntity<PostResponseDto> createPostForUser(
            final @PathVariable java.util.UUID userId,
            final @RequestBody @Valid PostCreateRequestDto post
    ) {
        if (userId == null) {
            log.info("Create post, post id is null");
            return ResponseEntity.notFound().build();
        }

        log.info("Create post [{}] for user id [{}]", post.getTitle(), userId);
        return ResponseEntity.ok(postService.createPostForUser(userId, post));
    }

    /**
     * Creates post.
     *
     * @param post created post
     * @return {@link ResponseEntity} for created post
     */
    @PostMapping
    public ResponseEntity<PostResponseDto> createPost(final @RequestBody @Valid PostCreateRequestDto post) {
        log.info("Create post: [{}]", post.getTitle());
        return ResponseEntity.ok(postService.createPost(post));
    }

    /**
     * Return post by id.
     *
     * @param id post id
     * @return post by id
     */
    @GetMapping("/{id}")
    public ResponseEntity<PostResponseDto> getPost(final @PathVariable java.util.UUID id) {
        log.info("Get post by id: {}", id);
        var postResponseDto = postService.getPost(id);
        return nonNull(postResponseDto) ? ResponseEntity.ok(postResponseDto) : ResponseEntity.notFound().build();
    }

    /**
     * Returns all Posts.
     *
     * @return all posts
     */
    @GetMapping
    public ResponseEntity<List<PostResponseDto>> getAll() {
        log.info("Get all posts");
        return ResponseEntity.ok(postService.getAllPosts());
    }

    /**
     * Returns concrete {@link Post} for this user.
     * @param userId id of this user
     * @param postId id of concrete post
     * @return {@link ResponseEntity} for post with specified id for this user
     */
    @GetMapping("/users/{userId}/{postId}")
    public ResponseEntity<PostResponseDto> getUserPost(
            final @PathVariable java.util.UUID userId,
            final @PathVariable java.util.UUID postId
    ) {
        if (userId == null) {
            log.info("Get user post, post id is null");
            return ResponseEntity.notFound().build();
        }

        log.info("Get user post for user id {} and post id {}", userId, postId);
        return ResponseEntity.ok(postService.getPostForUser(userId, postId));
    }

    /**
     * Returns all {@link Post} for this user.
     *
     * @param userId id of this user
     * @return {@link ResponseEntity} for all posts for this user
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity<List<PostResponseDto>> getUserPosts(final @PathVariable java.util.UUID userId) {
        if (userId != null) {
            log.info("Get all user posts for user id {}", userId);
            return ResponseEntity.ok(postService.getPostsForUser(userId));
        } else {
            log.info("Get all user posts, user id is null");
            return ResponseEntity.ok(postService.getAllPosts());
        }
    }

    /**
     * Updates post.
     *
     * @param postId if of concrete post
     * @param post updated post
     * @return {@link ResponseEntity} for updated post
     */
    @PutMapping("/{postId}")
    public ResponseEntity<PostResponseDto> updatePost(
            final @PathVariable java.util.UUID postId,
            final @RequestBody @Valid PostUpdateRequestDto post
    ) {
        log.info("Update post: [{}]", post.getTitle());
        return ResponseEntity.ok(postService.updatePost(postId, post));
    }

    /**
     * Deletes post.
     *
     * @param userId user id
     * @param postId post id
     * @return {@link ResponseEntity} for deleting operation
     */
    @DeleteMapping("/users/{userId}/{postId}")
    public ResponseEntity<Void> deletePost(
            final @PathVariable java.util.UUID userId,
            @PathVariable final java.util.UUID postId
    ) {
        if (userId == null) {
            log.info("Delete post, post id is null");
            return ResponseEntity.notFound().build();
        }

        log.info("Delete post for user id {} and post id {}", userId, postId);
        postService.deletePost(userId, postId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Deletes post.
     *
     * @param postId post id
     * @return {@link ResponseEntity} for deleting operation
     */
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable final java.util.UUID postId) {
        log.info("Delete post id {}", postId);
        postService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }
}

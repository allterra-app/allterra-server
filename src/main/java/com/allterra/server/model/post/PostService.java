package com.allterra.server.model.post;

import com.allterra.server.exception.ResourceNotFoundException;
import com.allterra.server.model.post.dto.PostResponseDto;
import com.allterra.server.model.post.dto.request.PostCreateRequestDto;
import com.allterra.server.model.post.dto.request.PostUpdateRequestDto;
import com.allterra.server.model.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * Service for {@link Post}.
 */
@Service
@RequiredArgsConstructor
public class PostService {
    private final UserRepository userRepository;
    private final PostMapper postMapper;
    private final PostRepository postRepository;

    /**
     * Creates new post for this user.
     *
     * @param createdPost created post
     * @return {@link PostResponseDto} for created post of this user
     */
    public PostResponseDto createPost(final PostCreateRequestDto createdPost) {
        final var postEntity = postMapper.toEntity(createdPost);
        final var userId = createdPost.getUserId();
        if (userId != null) {
            final var user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException(String.format("User with id %s not found", userId)));
            attachPostToUser(postEntity, user);
        }
        return postMapper.toDto(postRepository.save(postEntity));
    }

    /**
     * Creates new post for this user.
     *
     * @param userId id of this user
     * @param createdPost created post
     * @return {@link PostResponseDto} for created post of this user
     */
    public PostResponseDto createPostForUser(final java.util.UUID userId, final PostCreateRequestDto createdPost) {
        final var postEntity = postMapper.toEntity(createdPost);
        final var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("User with id %s not found", userId)));
        attachPostToUser(postEntity, user);
        return postMapper.toDto(postRepository.save(postEntity));
    }

    private void attachPostToUser(final Post post, final com.allterra.server.model.user.User user) {
        post.setUser(user);
        if (user.getPosts() != null && !user.getPosts().contains(post)) {
            user.getPosts().add(post);
        }
    }

    /**
     * Return Post by id.
     *
     * @param id post id
     * @return post by id
     */
    public PostResponseDto getPost(final java.util.UUID id) {
        return postRepository.findById(id).map(postMapper::toDto).orElse(null);
    }

    /**
     * Returns all Posts.
     *
     * @return all Posts
     */
    public List<PostResponseDto> getAllPosts() {
        return postRepository.findAll().stream().map(postMapper::toDto).toList();
    }

    /**
     * Returns concrete post for this user.
     *
     * @param userId id of this user
     * @param postId id of concrete post
     * @return {@link PostResponseDto} for concrete post for this user
     */
    public PostResponseDto getPostForUser(final java.util.UUID userId, final java.util.UUID postId) {
        return userRepository.findById(userId)
                .flatMap(user -> user.getPosts().stream()
                        .filter(post -> post.getId().equals(postId))
                        .findFirst()
                )
                .map(postMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Post with id %s not found", postId)));
    }

    /**
     * Returns all posts of this user.
     *
     * @param userId id of this user
     * @return {@link PostResponseDto} for all post of this user
     */
    public List<PostResponseDto> getPostsForUser(final java.util.UUID userId) {
        return userRepository.findById(userId).map(user ->
            user.getPosts().stream().map(postMapper::toDto).toList()
        ).orElseGet(Collections::emptyList);
    }

    /**
     * Updates post for this user.
     *
     * @param postId id of concrete post
     * @param updatedPost updated post
     * @return {@link PostResponseDto} for updated post of this user
     */
    public PostResponseDto updatePost(final java.util.UUID postId, final PostUpdateRequestDto updatedPost) {
        return postRepository.findById(postId)
                .map(post -> {
                    postMapper.updateEntityFromDto(updatedPost, post);
                    var savedPost = postRepository.save(post);
                    return postMapper.toDto(savedPost);
                })
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Post with id %s not found", postId)));
    }

    /**
     * Deletes post.
     *
     * @param userId user id
     * @param postId post id
     */
    public void deletePost(final java.util.UUID userId, final java.util.UUID postId) {
        final var post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Post with id %s not found", postId)));
        userRepository.findById(userId).ifPresent(user -> user.getPosts().remove(post));
        postRepository.delete(post);
    }

    /**
     * Deletes post.
     *
     * @param postId post id
     */
    public void deletePost(final java.util.UUID postId) {
        final var post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Post with id %s not found", postId)));
        postRepository.delete(post);
    }
}

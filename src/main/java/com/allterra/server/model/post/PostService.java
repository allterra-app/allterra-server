package com.allterra.server.model.post;

import com.allterra.server.exception.ResourceNotFoundException;
import com.allterra.server.model.notification.NotificationService;
import com.allterra.server.model.post.dto.FeedPageResponseDto;
import com.allterra.server.model.post.dto.PostResponseDto;
import com.allterra.server.model.post.dto.request.PostCreateRequestDto;
import com.allterra.server.model.post.dto.request.PostUpdateRequestDto;
import com.allterra.server.model.route.RouteRepository;
import com.allterra.server.model.trip.TripRepository;
import com.allterra.server.model.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for {@link Post}.
 */
@Service
@RequiredArgsConstructor
@SuppressWarnings("checkstyle:DesignForExtension")
public class PostService {
    private final UserRepository userRepository;
    private final PostMapper postMapper;
    private final PostRepository postRepository;
    private final TripRepository tripRepository;
    private final RouteRepository routeRepository;
    private final NotificationService notificationService;
    private final PostLikeService postLikeService;

    /**
     * Creates new post for this user.
     *
     * @param createdPost created post
     * @return {@link PostResponseDto} for created post of this user
     */
    public PostResponseDto createPost(final PostCreateRequestDto createdPost) {
        validateReferences(createdPost.getTripId(), createdPost.getRouteId());
        final var postEntity = postMapper.toEntity(createdPost);
        final var userId = createdPost.getUserId();
        if (userId != null) {
            final var user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException(String.format("User with id %s not found", userId)));
            attachPostToUser(postEntity, user);
            postEntity.setAudience(resolveAudience(createdPost.getAudience()));
            final var saved = postRepository.save(postEntity);
            notificationService.createForUser(user, "Post published", postEntity.getTitle());
            return postMapper.toDto(saved);
        }
        postEntity.setAudience(resolveAudience(createdPost.getAudience()));
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
        validateReferences(createdPost.getTripId(), createdPost.getRouteId());
        final var postEntity = postMapper.toEntity(createdPost);
        final var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("User with id %s not found", userId)));
        attachPostToUser(postEntity, user);
        postEntity.setAudience(resolveAudience(createdPost.getAudience()));
        final var saved = postRepository.save(postEntity);
        notificationService.createForUser(user, "Post published", postEntity.getTitle());
        return postMapper.toDto(saved);
    }

    private void attachPostToUser(final Post post, final com.allterra.server.model.user.User user) {
        post.setUser(user);
        if (user.getPosts() == null) {
            user.setPosts(new java.util.ArrayList<>());
        }
        if (!user.getPosts().contains(post)) {
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
        final var dto = postRepository.findById(id).map(postMapper::toDto).orElse(null);
        if (dto == null) {
            return null;
        }
        final var enriched = enrichWithLikeInfo(List.of(dto));
        return enriched.isEmpty() ? dto : enriched.get(0);
    }

    /**
     * Returns all Posts.
     *
     * @return all Posts
     */
    public List<PostResponseDto> getAllPosts() {
        final var posts = postRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(postMapper::toDto)
                .toList();
        return enrichWithLikeInfo(posts);
    }

    /**
     * Returns paginated community feed.
     *
     * @param page zero-based page number
     * @param size page size
     * @return paginated feed response
     */
    public FeedPageResponseDto getFeed(final int page, final int size, final PostAudience audience) {
        final int normalizedPage = Math.max(page, 0);
        final int normalizedSize = Math.min(Math.max(size, 1), 50);
        final var feedPage = audience == null
                ? postRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(normalizedPage, normalizedSize))
                : postRepository.findAllByAudienceOrderByCreatedAtDesc(audience, PageRequest.of(normalizedPage, normalizedSize));
        final var items = feedPage.getContent().stream().map(postMapper::toDto).toList();
        final var enrichedItems = enrichWithLikeInfo(items);
        return FeedPageResponseDto.builder()
                .items(enrichedItems)
                .page(feedPage.getNumber())
                .size(feedPage.getSize())
                .totalItems(feedPage.getTotalElements())
                .hasNext(feedPage.hasNext())
                .build();
    }

    /**
     * Returns concrete post for this user.
     *
     * @param userId id of this user
     * @param postId id of concrete post
     * @return {@link PostResponseDto} for concrete post for this user
     */
    public PostResponseDto getPostForUser(final java.util.UUID userId, final java.util.UUID postId) {
        final var dto = userRepository.findById(userId)
                .flatMap(user -> user.getPosts().stream()
                        .filter(post -> post.getId().equals(postId))
                        .findFirst()
                )
                .map(postMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Post with id %s not found", postId)));
        final var enriched = enrichWithLikeInfo(List.of(dto));
        return enriched.isEmpty() ? dto : enriched.get(0);
    }

    /**
     * Returns all posts of this user.
     *
     * @param userId id of this user
     * @return {@link PostResponseDto} for all post of this user
     */
    public List<PostResponseDto> getPostsForUser(final java.util.UUID userId) {
        if (!userRepository.existsById(userId)) {
            return Collections.emptyList();
        }
        final var posts = postRepository.findAllByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(postMapper::toDto)
                .toList();
        return enrichWithLikeInfo(posts);
    }

    /**
     * Returns saved posts for this user.
     *
     * @param userId user id
     * @return saved posts for this user
     */
    public List<PostResponseDto> getSavedPostsForUser(final UUID userId) {
        final var posts = userRepository.findById(userId)
                .map(user -> {
                    final var savedPosts = user.getSavedPosts();
                    if (savedPosts == null) {
                        return Collections.<PostResponseDto>emptyList();
                    }
                    return savedPosts.stream()
                            .map(postMapper::toDto)
                            .toList();
                })
                .orElseGet(Collections::emptyList);
        return enrichWithLikeInfo(posts);
    }

    /**
     * Saves post for this user.
     *
     * @param userId user id
     * @param postId post id
     */
    public void savePostForUser(final UUID userId, final UUID postId) {
        final var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("User with id %s not found", userId)));
        final var post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Post with id %s not found", postId)));
        if (user.getSavedPosts() == null) {
            user.setSavedPosts(new java.util.ArrayList<>());
        }
        if (user.getSavedPosts().stream().noneMatch(existing -> existing.getId().equals(postId))) {
            user.getSavedPosts().add(post);
            userRepository.save(user);
        }
    }

    /**
     * Removes saved post for this user.
     *
     * @param userId user id
     * @param postId post id
     */
    public void unsavePostForUser(final UUID userId, final UUID postId) {
        final var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("User with id %s not found", userId)));
        if (user.getSavedPosts() == null) {
            return;
        }
        user.getSavedPosts().removeIf(post -> post.getId().equals(postId));
        userRepository.save(user);
    }

    /**
     * Updates post for this user.
     *
     * @param postId id of concrete post
     * @param updatedPost updated post
     * @return {@link PostResponseDto} for updated post of this user
     */
    public PostResponseDto updatePost(final java.util.UUID postId, final PostUpdateRequestDto updatedPost) {
        validateReferences(updatedPost.getTripId(), updatedPost.getRouteId());
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
        final var postUserId = post.getUser() == null ? null : post.getUser().getId();
        if (postUserId == null || !postUserId.equals(userId)) {
            throw new ResourceNotFoundException(
                    String.format("Post with id %s not found for user %s", postId, userId)
            );
        }
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

    private void validateReferences(final UUID tripId, final UUID routeId) {
        if (tripId != null && !tripRepository.existsById(tripId)) {
            throw new ResourceNotFoundException(String.format("Trip with id %s not found", tripId));
        }
        if (routeId != null && !routeRepository.existsById(routeId)) {
            throw new ResourceNotFoundException(String.format("Route with id %s not found", routeId));
        }
    }

    private PostAudience resolveAudience(final PostAudience audience) {
        return audience == null ? PostAudience.PUBLIC : audience;
    }

    /**
     * Enriches a list of post DTOs with like counts and current user's liked status.
     */
    private List<PostResponseDto> enrichWithLikeInfo(final List<PostResponseDto> dtos) {
        if (dtos == null || dtos.isEmpty()) {
            return dtos != null ? dtos : List.of();
        }
        final var postIds = dtos.stream()
                .map(PostResponseDto::getId)
                .collect(Collectors.toSet());
        final var likeCounts = postLikeService.getLikeCounts(postIds);
        final var currentUserId = resolveCurrentUserId();
        final Set<UUID> likedByCurrentUser = currentUserId != null
                ? postLikeService.getLikedByUser(currentUserId, postIds)
                : Set.of();

        return dtos.stream()
                .map(dto -> {
                    dto.setLikeCount(likeCounts.getOrDefault(dto.getId(), 0));
                    dto.setLiked(likedByCurrentUser.contains(dto.getId()));
                    return dto;
                })
                .toList();
    }

    /**
     * Resolves the currently authenticated user's ID from Spring Security context.
     *
     * @return user ID or null if not authenticated
     */
    private UUID resolveCurrentUserId() {
        final var auth = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }
        final var email = auth.getName();
        if (email == null || email.isBlank()) {
            return null;
        }
        return userRepository.findByEmailIgnoreCase(email)
                .map(com.allterra.server.model.user.User::getId)
                .orElse(null);
    }
}

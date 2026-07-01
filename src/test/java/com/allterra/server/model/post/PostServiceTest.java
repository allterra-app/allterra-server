package com.allterra.server.model.post;

import com.allterra.server.exception.ResourceNotFoundException;
import com.allterra.server.model.notification.NotificationService;
import com.allterra.server.model.post.dto.FeedPageResponseDto;
import com.allterra.server.model.post.dto.PostResponseDto;
import com.allterra.server.model.post.dto.request.PostCreateRequestDto;
import com.allterra.server.model.post.dto.request.PostUpdateRequestDto;
import com.allterra.server.model.route.RouteRepository;
import com.allterra.server.model.trip.TripRepository;
import com.allterra.server.model.user.User;
import com.allterra.server.model.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PostMapper postMapper;
    @Mock
    private PostRepository postRepository;
    @Mock
    private TripRepository tripRepository;
    @Mock
    private RouteRepository routeRepository;
    @Mock
    private NotificationService notificationService;
    @Mock
    private PostLikeService postLikeService;

    @InjectMocks
    private PostService postService;

    @Test
    void createPostShouldMapAndSave() {
        var request = PostCreateRequestDto.builder().title("title").build();
        var entity = Post.builder().title("title").build();
        var saved = Post.builder().id(com.allterra.server.TestUuids.id(1)).title("title").build();
        var response = PostResponseDto.builder().id(com.allterra.server.TestUuids.id(1)).title("title").build();

        when(postMapper.toEntity(request)).thenReturn(entity);
        when(postRepository.save(entity)).thenReturn(saved);
        when(postMapper.toDto(saved)).thenReturn(response);

        assertThat(postService.createPost(request)).isEqualTo(response);
    }

    @Test
    void createPostShouldThrowWhenTripReferenceMissing() {
        var tripId = com.allterra.server.TestUuids.id(7);
        var request = PostCreateRequestDto.builder().title("title").tripId(tripId).build();

        when(tripRepository.existsById(tripId)).thenReturn(false);

        assertThatThrownBy(() -> postService.createPost(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Trip with id " + tripId + " not found");
    }

    @Test
    void createPostForUserShouldAddPostToUserIfPresent() {
        var request = PostCreateRequestDto.builder().title("title").build();
        var entity = Post.builder().title("title").build();
        var user = User.builder().posts(new ArrayList<>()).build();
        var response = PostResponseDto.builder().id(com.allterra.server.TestUuids.id(1)).title("title").build();

        when(postMapper.toEntity(request)).thenReturn(entity);
        when(userRepository.findById(com.allterra.server.TestUuids.id(3))).thenReturn(Optional.of(user));
        when(postRepository.save(entity)).thenReturn(entity);
        when(postMapper.toDto(entity)).thenReturn(response);

        var result = postService.createPostForUser(com.allterra.server.TestUuids.id(3), request);

        assertThat(result).isEqualTo(response);
        assertThat(user.getPosts()).contains(entity);
    }

    @Test
    void getPostShouldReturnNullWhenMissing() {
        when(postRepository.findById(com.allterra.server.TestUuids.id(9))).thenReturn(Optional.empty());

        assertThat(postService.getPost(com.allterra.server.TestUuids.id(9))).isNull();
    }

    @Test
    void getPostForUserShouldThrowWhenPostNotFound() {
        when(userRepository.findById(com.allterra.server.TestUuids.id(1))).thenReturn(Optional.of(User.builder().posts(List.of()).build()));

        assertThatThrownBy(() -> postService.getPostForUser(com.allterra.server.TestUuids.id(1), com.allterra.server.TestUuids.id(2)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Post with id " + com.allterra.server.TestUuids.id(2) + " not found");
    }

    @Test
    void getPostsForUserShouldReturnEmptyWhenUserMissing() {
        when(userRepository.existsById(com.allterra.server.TestUuids.id(1))).thenReturn(false);

        assertThat(postService.getPostsForUser(com.allterra.server.TestUuids.id(1))).isEmpty();
    }

    @Test
    void getFeedShouldReturnPaginatedResponse() {
        var post = Post.builder().id(com.allterra.server.TestUuids.id(9)).title("Feed").build();
        var dto = PostResponseDto.builder().id(com.allterra.server.TestUuids.id(9)).title("Feed").build();

        when(postRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(0, 20)))
                .thenReturn(new PageImpl<>(List.of(post), PageRequest.of(0, 20), 1));
        when(postMapper.toDto(post)).thenReturn(dto);
        when(postLikeService.getLikeCounts(java.util.Set.of(com.allterra.server.TestUuids.id(9))))
                .thenReturn(java.util.Map.of());

        FeedPageResponseDto result = postService.getFeed(0, 20, null);

        assertThat(result.getItems()).containsExactly(dto);
        assertThat(result.getPage()).isEqualTo(0);
        assertThat(result.getSize()).isEqualTo(20);
        assertThat(result.getTotalItems()).isEqualTo(1);
        assertThat(result.isHasNext()).isFalse();
    }

    @Test
    void updatePostShouldThrowWhenMissing() {
        when(postRepository.findById(com.allterra.server.TestUuids.id(5))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> postService.updatePost(com.allterra.server.TestUuids.id(5), new PostUpdateRequestDto()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Post with id " + com.allterra.server.TestUuids.id(5) + " not found");
    }

    @Test
    void deletePostShouldDeleteWhenOwnedByUser() {
        var userId = com.allterra.server.TestUuids.id(1);
        var post = Post.builder()
                .id(com.allterra.server.TestUuids.id(5))
                .user(User.builder().id(userId).build())
                .build();

        when(postRepository.findById(com.allterra.server.TestUuids.id(5))).thenReturn(Optional.of(post));

        postService.deletePost(userId, com.allterra.server.TestUuids.id(5));

        verify(postRepository).delete(post);
    }

    @Test
    void deletePostShouldThrowWhenPostIsNotOwnedByUser() {
        var ownerId = com.allterra.server.TestUuids.id(2);
        var post = Post.builder()
                .id(com.allterra.server.TestUuids.id(5))
                .user(User.builder().id(ownerId).build())
                .build();

        when(postRepository.findById(com.allterra.server.TestUuids.id(5))).thenReturn(Optional.of(post));

        assertThatThrownBy(() -> postService.deletePost(com.allterra.server.TestUuids.id(1), com.allterra.server.TestUuids.id(5)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Post with id " + com.allterra.server.TestUuids.id(5) + " not found for user");
        verify(postRepository, never()).delete(post);
    }

    @Test
    void deletePostShouldDeleteByIdWhenExists() {
        var post = Post.builder().id(com.allterra.server.TestUuids.id(5)).build();
        when(postRepository.findById(com.allterra.server.TestUuids.id(5))).thenReturn(Optional.of(post));

        postService.deletePost(com.allterra.server.TestUuids.id(5));

        verify(postRepository).delete(post);
    }
}

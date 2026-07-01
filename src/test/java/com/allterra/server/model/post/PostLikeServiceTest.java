package com.allterra.server.model.post;

import com.allterra.server.exception.ResourceNotFoundException;
import com.allterra.server.model.user.User;
import com.allterra.server.model.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostLikeServiceTest {

    @Mock
    private JdbcTemplate jdbcTemplate;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostLikeService postLikeService;

    @Test
    void likeShouldThrowWhenUserNotFound() {
        var userId = com.allterra.server.TestUuids.id(1);
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThatThrownBy(() -> postLikeService.like(userId, com.allterra.server.TestUuids.id(2)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void likeShouldThrowWhenPostNotFound() {
        var userId = com.allterra.server.TestUuids.id(1);
        var postId = com.allterra.server.TestUuids.id(2);
        when(userRepository.existsById(userId)).thenReturn(true);
        when(postRepository.existsById(postId)).thenReturn(false);

        assertThatThrownBy(() -> postLikeService.like(userId, postId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Post not found");
    }

    @Test
    void isLikedByShouldReturnFalseWhenUserIdNull() {
        assertThat(postLikeService.isLikedBy(null, UUID.randomUUID())).isFalse();
    }

    @Test
    void getLikeCountsShouldReturnEmptyForNullInput() {
        assertThat(postLikeService.getLikeCounts(null)).isEmpty();
    }

    @Test
    void getLikedByUserShouldReturnEmptyForNullUserId() {
        assertThat(postLikeService.getLikedByUser(null, java.util.Set.of(UUID.randomUUID()))).isEmpty();
    }
}

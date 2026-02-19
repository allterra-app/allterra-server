package com.allterra.server.model.post;

import com.allterra.server.exception.ResourceNotFoundException;
import com.allterra.server.model.post.dto.PostResponseDto;
import com.allterra.server.model.post.dto.request.PostCreateRequestDto;
import com.allterra.server.model.post.dto.request.PostUpdateRequestDto;
import com.allterra.server.model.user.User;
import com.allterra.server.model.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
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
        when(userRepository.findById(com.allterra.server.TestUuids.id(1))).thenReturn(Optional.empty());

        assertThat(postService.getPostsForUser(com.allterra.server.TestUuids.id(1))).isEmpty();
    }

    @Test
    void updatePostShouldThrowWhenMissing() {
        when(postRepository.findById(com.allterra.server.TestUuids.id(5))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> postService.updatePost(com.allterra.server.TestUuids.id(5), new PostUpdateRequestDto()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Post with id " + com.allterra.server.TestUuids.id(5) + " not found");
    }

    @Test
    void deletePostShouldRemoveFromUserAndDelete() {
        var post = Post.builder().id(com.allterra.server.TestUuids.id(5)).build();
        var user = User.builder().posts(new ArrayList<>(List.of(post))).build();

        when(postRepository.findById(com.allterra.server.TestUuids.id(5))).thenReturn(Optional.of(post));
        when(userRepository.findById(com.allterra.server.TestUuids.id(1))).thenReturn(Optional.of(user));

        postService.deletePost(com.allterra.server.TestUuids.id(1), com.allterra.server.TestUuids.id(5));

        assertThat(user.getPosts()).doesNotContain(post);
        verify(postRepository).delete(post);
    }

    @Test
    void deletePostShouldDeleteByIdWhenExists() {
        var post = Post.builder().id(com.allterra.server.TestUuids.id(5)).build();
        when(postRepository.findById(com.allterra.server.TestUuids.id(5))).thenReturn(Optional.of(post));

        postService.deletePost(com.allterra.server.TestUuids.id(5));

        verify(postRepository).delete(post);
    }
}

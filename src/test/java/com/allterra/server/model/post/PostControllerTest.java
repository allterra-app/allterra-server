package com.allterra.server.model.post;

import com.allterra.server.model.post.dto.PostResponseDto;
import com.allterra.server.model.post.dto.request.PostCreateRequestDto;
import com.allterra.server.model.post.dto.request.PostUpdateRequestDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostControllerTest {

    @Mock
    private PostService postService;

    @InjectMocks
    private PostController postController;

    @Test
    void createPostShouldReturnOk() {
        var request = PostCreateRequestDto.builder().title("title").build();
        var response = PostResponseDto.builder().id(com.allterra.server.TestUuids.id(1)).title("title").build();
        when(postService.createPost(request)).thenReturn(response);

        var result = postController.createPost(request);

        assertThat(result.getStatusCode().value()).isEqualTo(200);
        assertThat(result.getBody()).isEqualTo(response);
    }

    @Test
    void createPostForUserShouldReturnOk() {
        var request = PostCreateRequestDto.builder().title("title").build();
        var response = PostResponseDto.builder().id(com.allterra.server.TestUuids.id(1)).title("title").build();
        when(postService.createPostForUser(com.allterra.server.TestUuids.id(3), request)).thenReturn(response);

        var result = postController.createPostForUser(com.allterra.server.TestUuids.id(3), request);

        assertThat(result.getStatusCode().value()).isEqualTo(200);
        assertThat(result.getBody()).isEqualTo(response);
    }

    @Test
    void getPostShouldReturnNotFoundWhenServiceReturnsNull() {
        when(postService.getPost(com.allterra.server.TestUuids.id(2))).thenReturn(null);

        var result = postController.getPost(com.allterra.server.TestUuids.id(2));

        assertThat(result.getStatusCode().value()).isEqualTo(404);
    }

    @Test
    void getAllShouldReturnPosts() {
        var response = PostResponseDto.builder().id(com.allterra.server.TestUuids.id(1)).build();
        when(postService.getAllPosts()).thenReturn(List.of(response));

        var result = postController.getAll();

        assertThat(result.getStatusCode().value()).isEqualTo(200);
        assertThat(result.getBody()).containsExactly(response);
    }

    @Test
    void getUserPostShouldReturnOk() {
        var response = PostResponseDto.builder().id(com.allterra.server.TestUuids.id(4)).build();
        when(postService.getPostForUser(com.allterra.server.TestUuids.id(1), com.allterra.server.TestUuids.id(4))).thenReturn(response);

        var result = postController.getUserPost(com.allterra.server.TestUuids.id(1), com.allterra.server.TestUuids.id(4));

        assertThat(result.getStatusCode().value()).isEqualTo(200);
        assertThat(result.getBody()).isEqualTo(response);
    }

    @Test
    void updatePostShouldReturnUpdatedPost() {
        var request = PostUpdateRequestDto.builder().title("new").build();
        var response = PostResponseDto.builder().id(com.allterra.server.TestUuids.id(4)).title("new").build();
        when(postService.updatePost(com.allterra.server.TestUuids.id(4), request)).thenReturn(response);

        var result = postController.updatePost(com.allterra.server.TestUuids.id(4), request);

        assertThat(result.getStatusCode().value()).isEqualTo(200);
        assertThat(result.getBody()).isEqualTo(response);
    }

    @Test
    void deletePostByUserShouldReturnNoContent() {
        var result = postController.deletePost(com.allterra.server.TestUuids.id(1), com.allterra.server.TestUuids.id(2));

        assertThat(result.getStatusCode().value()).isEqualTo(204);
        verify(postService).deletePost(com.allterra.server.TestUuids.id(1), com.allterra.server.TestUuids.id(2));
    }

    @Test
    void deletePostShouldReturnNoContent() {
        var result = postController.deletePost(com.allterra.server.TestUuids.id(2));

        assertThat(result.getStatusCode().value()).isEqualTo(204);
        verify(postService).deletePost(com.allterra.server.TestUuids.id(2));
    }
}

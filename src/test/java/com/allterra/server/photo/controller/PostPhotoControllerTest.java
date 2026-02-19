package com.allterra.server.photo.controller;

import com.allterra.server.photo.dto.PostPhotoResponseDto;
import com.allterra.server.photo.dto.request.create.PostPhotoCreateRequestDto;
import com.allterra.server.photo.dto.request.update.PostPhotoUpdateRequestDto;
import com.allterra.server.photo.model.PostPhoto;
import com.allterra.server.photo.service.PhotoService;
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
class PostPhotoControllerTest {

    @Mock
    private PhotoService<PostPhoto, PostPhotoResponseDto, PostPhotoCreateRequestDto, PostPhotoUpdateRequestDto> service;

    @InjectMocks
    private PostPhotoController controller;

    @Test
    void createShouldReturnOk() {
        var request = PostPhotoCreateRequestDto.builder().url("/p.jpg").build();
        var response = PostPhotoResponseDto.builder().id(com.allterra.server.TestUuids.id(1)).url("/p.jpg").build();
        when(service.create(request)).thenReturn(response);

        var result = controller.create(request);

        assertThat(result.getStatusCode().value()).isEqualTo(200);
        assertThat(result.getBody()).isEqualTo(response);
    }

    @Test
    void getAllShouldReturnList() {
        var response = PostPhotoResponseDto.builder().id(com.allterra.server.TestUuids.id(1)).build();
        when(service.getAll()).thenReturn(List.of(response));

        var result = controller.getAll();

        assertThat(result.getStatusCode().value()).isEqualTo(200);
        assertThat(result.getBody()).containsExactly(response);
    }

    @Test
    void deleteShouldReturnNoContent() {
        var result = controller.delete(com.allterra.server.TestUuids.id(8));

        assertThat(result.getStatusCode().value()).isEqualTo(204);
        verify(service).delete(com.allterra.server.TestUuids.id(8));
    }
}

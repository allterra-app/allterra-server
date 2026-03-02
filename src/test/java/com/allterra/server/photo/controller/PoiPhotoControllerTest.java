package com.allterra.server.photo.controller;

import com.allterra.server.photo.dto.PoiPhotoResponseDto;
import com.allterra.server.photo.dto.request.create.PoiPhotoCreateRequestDto;
import com.allterra.server.photo.dto.request.update.PoiPhotoUpdateRequestDto;
import com.allterra.server.photo.model.PoiPhoto;
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
class PoiPhotoControllerTest {

    @Mock
    private PhotoService<PoiPhoto, PoiPhotoResponseDto, PoiPhotoCreateRequestDto, PoiPhotoUpdateRequestDto> service;

    @InjectMocks
    private PoiPhotoController controller;

    @Test
    void createShouldReturnOk() {
        var request = PoiPhotoCreateRequestDto.builder().url("/poi.jpg").build();
        var response = PoiPhotoResponseDto.builder().id(com.allterra.server.TestUuids.id(1)).url("/poi.jpg").build();
        when(service.create(request)).thenReturn(response);

        var result = controller.create(request);

        assertThat(result.getStatusCode().value()).isEqualTo(200);
        assertThat(result.getBody()).isEqualTo(response);
    }

    @Test
    void getAllShouldReturnList() {
        var response = PoiPhotoResponseDto.builder().id(com.allterra.server.TestUuids.id(1)).build();
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

package com.allterra.server.photo.service;

import com.allterra.server.model.poi.Poi;
import com.allterra.server.model.poi.PoiRepository;
import com.allterra.server.photo.dto.PoiPhotoResponseDto;
import com.allterra.server.photo.dto.request.create.PoiPhotoCreateRequestDto;
import com.allterra.server.photo.mapper.PoiPhotoMapper;
import com.allterra.server.photo.model.PoiPhoto;
import com.allterra.server.photo.repository.PoiPhotoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PoiPhotoServiceTest {

    @Mock
    private PoiPhotoRepository repository;
    @Mock
    private PoiPhotoMapper mapper;
    @Mock
    private PoiRepository poiRepository;

    @InjectMocks
    private PoiPhotoService service;

    @Test
    void createShouldDelegateToBasePhotoServiceFlow() {
        var request = PoiPhotoCreateRequestDto.builder()
                .url("/poi.jpg")
                .poiId(com.allterra.server.TestUuids.id(20))
                .build();
        var entity = PoiPhoto.builder().url("/poi.jpg").build();
        var saved = PoiPhoto.builder().id(com.allterra.server.TestUuids.id(2)).url("/poi.jpg").build();
        var response = PoiPhotoResponseDto.builder().id(com.allterra.server.TestUuids.id(2)).url("/poi.jpg").build();

        when(poiRepository.findById(com.allterra.server.TestUuids.id(20))).thenReturn(Optional.of(Poi.builder().id(com.allterra.server.TestUuids.id(20)).build()));
        when(mapper.toEntity(request)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(saved);
        when(mapper.toDto(saved)).thenReturn(response);

        assertThat(service.create(request)).isEqualTo(response);
    }
}

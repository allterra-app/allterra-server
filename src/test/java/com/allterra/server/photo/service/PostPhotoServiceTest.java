package com.allterra.server.photo.service;

import com.allterra.server.exception.ResourceNotFoundException;
import com.allterra.server.photo.dto.PostPhotoResponseDto;
import com.allterra.server.photo.dto.request.create.PostPhotoCreateRequestDto;
import com.allterra.server.photo.dto.request.update.PostPhotoUpdateRequestDto;
import com.allterra.server.photo.mapper.PostPhotoMapper;
import com.allterra.server.photo.model.PostPhoto;
import com.allterra.server.photo.repository.PostPhotoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostPhotoServiceTest {

    @Mock
    private PostPhotoRepository repository;
    @Mock
    private PostPhotoMapper mapper;

    @InjectMocks
    private PostPhotoService service;

    @Test
    void createShouldMapSaveAndReturnDto() {
        var request = PostPhotoCreateRequestDto.builder().url("/img.jpg").build();
        var entity = PostPhoto.builder().url("/img.jpg").build();
        var saved = PostPhoto.builder().id(com.allterra.server.TestUuids.id(1)).url("/img.jpg").build();
        var response = PostPhotoResponseDto.builder().id(com.allterra.server.TestUuids.id(1)).url("/img.jpg").build();

        when(mapper.toEntity(request)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(saved);
        when(mapper.toDto(saved)).thenReturn(response);

        assertThat(service.create(request)).isEqualTo(response);
    }

    @Test
    void getShouldThrowWhenMissing() {
        when(repository.findById(com.allterra.server.TestUuids.id(4))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.get(com.allterra.server.TestUuids.id(4)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Photo not found with id: " + com.allterra.server.TestUuids.id(4));
    }

    @Test
    void getAllShouldReturnMappedDtos() {
        var entity = PostPhoto.builder().id(com.allterra.server.TestUuids.id(1)).build();
        var dto = PostPhotoResponseDto.builder().id(com.allterra.server.TestUuids.id(1)).build();

        when(repository.findAll()).thenReturn(List.of(entity));
        when(mapper.toDto(entity)).thenReturn(dto);

        assertThat(service.getAll()).containsExactly(dto);
    }

    @Test
    void updateShouldModifyAndReturnDto() {
        var existing = PostPhoto.builder().id(com.allterra.server.TestUuids.id(7)).url("old").build();
        var request = PostPhotoUpdateRequestDto.builder().id(com.allterra.server.TestUuids.id(7)).url("new").build();
        var dto = PostPhotoResponseDto.builder().id(com.allterra.server.TestUuids.id(7)).url("new").build();

        when(repository.findById(com.allterra.server.TestUuids.id(7))).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);
        when(mapper.toDto(existing)).thenReturn(dto);

        assertThat(service.update(com.allterra.server.TestUuids.id(7), request)).isEqualTo(dto);
        verify(mapper).updateEntityFromDto(request, existing);
    }

    @Test
    void deleteShouldThrowWhenMissing() {
        when(repository.existsById(com.allterra.server.TestUuids.id(8))).thenReturn(false);

        assertThatThrownBy(() -> service.delete(com.allterra.server.TestUuids.id(8)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Photo not found with id: " + com.allterra.server.TestUuids.id(8));
    }

    @Test
    void deleteShouldDelegateWhenExists() {
        when(repository.existsById(com.allterra.server.TestUuids.id(8))).thenReturn(true);

        service.delete(com.allterra.server.TestUuids.id(8));

        verify(repository).deleteById(com.allterra.server.TestUuids.id(8));
    }
}

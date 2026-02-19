package com.allterra.server.photo.service;

import com.allterra.server.photo.dto.UserPhotoResponseDto;
import com.allterra.server.photo.dto.request.create.UserPhotoCreateRequestDto;
import com.allterra.server.photo.mapper.UserPhotoMapper;
import com.allterra.server.photo.model.UserPhoto;
import com.allterra.server.photo.repository.UserPhotoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserPhotoServiceTest {

    @Mock
    private UserPhotoRepository repository;
    @Mock
    private UserPhotoMapper mapper;

    @InjectMocks
    private UserPhotoService service;

    @Test
    void createShouldDelegateToBasePhotoServiceFlow() {
        var request = UserPhotoCreateRequestDto.builder().url("/user.jpg").build();
        var entity = UserPhoto.builder().url("/user.jpg").build();
        var saved = UserPhoto.builder().id(com.allterra.server.TestUuids.id(3)).url("/user.jpg").build();
        var response = UserPhotoResponseDto.builder().id(com.allterra.server.TestUuids.id(3)).url("/user.jpg").build();

        when(mapper.toEntity(request)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(saved);
        when(mapper.toDto(saved)).thenReturn(response);

        assertThat(service.create(request)).isEqualTo(response);
    }
}

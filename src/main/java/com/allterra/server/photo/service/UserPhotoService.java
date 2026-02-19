package com.allterra.server.photo.service;

import com.allterra.server.photo.dto.UserPhotoResponseDto;
import com.allterra.server.photo.dto.request.create.UserPhotoCreateRequestDto;
import com.allterra.server.photo.dto.request.update.UserPhotoUpdateRequestDto;
import com.allterra.server.photo.mapper.UserPhotoMapper;
import com.allterra.server.photo.model.UserPhoto;
import com.allterra.server.photo.repository.UserPhotoRepository;
import org.springframework.stereotype.Service;

/**
 * Service for {@link UserPhoto}.
 */
@Service
public class UserPhotoService extends PhotoService<UserPhoto, UserPhotoResponseDto, UserPhotoCreateRequestDto, UserPhotoUpdateRequestDto> {
    public UserPhotoService(final UserPhotoRepository repository, final UserPhotoMapper mapper) {
        super(repository, mapper);
    }
}

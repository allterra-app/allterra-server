package com.allterra.server.photo.mapper;

import com.allterra.server.photo.dto.UserPhotoResponseDto;
import com.allterra.server.photo.dto.request.create.UserPhotoCreateRequestDto;
import com.allterra.server.photo.dto.request.update.UserPhotoUpdateRequestDto;
import com.allterra.server.photo.model.UserPhoto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * {@link PhotoMapper} implementation for {@link UserPhoto}.
 */
@Mapper(componentModel = "spring")
public interface UserPhotoMapper extends
        PhotoMapper<UserPhoto, UserPhotoResponseDto, UserPhotoCreateRequestDto, UserPhotoUpdateRequestDto> {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    UserPhoto toEntity(final UserPhotoCreateRequestDto createRequestDto);

    UserPhotoResponseDto toDto(final UserPhoto entity);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    void updateEntityFromDto(final UserPhotoUpdateRequestDto updateRequestDto, @MappingTarget final UserPhoto entity);
}

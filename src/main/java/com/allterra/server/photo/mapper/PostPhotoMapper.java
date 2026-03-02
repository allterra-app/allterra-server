package com.allterra.server.photo.mapper;

import com.allterra.server.photo.dto.PostPhotoResponseDto;
import com.allterra.server.photo.dto.request.create.PostPhotoCreateRequestDto;
import com.allterra.server.photo.dto.request.update.PostPhotoUpdateRequestDto;
import com.allterra.server.photo.model.PostPhoto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * {@link PhotoMapper} implementation for {@link PostPhoto}.
 */
@Mapper(componentModel = "spring")
public interface PostPhotoMapper extends
        PhotoMapper<PostPhoto, PostPhotoResponseDto, PostPhotoCreateRequestDto, PostPhotoUpdateRequestDto> {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "post", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    PostPhoto toEntity(final PostPhotoCreateRequestDto createRequestDto);

    @Mapping(target = "postId", source = "post.id")
    PostPhotoResponseDto toDto(final PostPhoto entity);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    void updateEntityFromDto(final PostPhotoUpdateRequestDto updateRequestDto, final @MappingTarget PostPhoto entity);
}

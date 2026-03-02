package com.allterra.server.model.post;

import com.allterra.server.model.post.dto.PostResponseDto;
import com.allterra.server.model.post.dto.PostUserResponseDto;
import com.allterra.server.model.post.dto.request.PostCreateRequestDto;
import com.allterra.server.model.post.dto.request.PostUpdateRequestDto;
import com.allterra.server.photo.dto.PostPhotoResponseDto;
import com.allterra.server.photo.model.PostPhoto;
import com.allterra.server.model.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.Collections;
import java.util.List;

/**
 * Mapper for {@link Post}.
 */
@Mapper(componentModel = "spring")
public interface PostMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "photos", ignore = true)
    Post toEntity(final PostCreateRequestDto postCreateRequestDto);

    PostResponseDto toDto(final Post post);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    void updateEntityFromDto(final PostUpdateRequestDto postUpdateRequestDto, final @MappingTarget Post post);

    /**
     * Maps post user entity to lightweight response model.
     *
     * @param user post owner entity
     * @return post user reference response
     */
    default PostUserResponseDto mapUser(final User user) {
        if (user == null) {
            return null;
        }
        return PostUserResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    /**
     * Maps post photo entities to response models.
     *
     * @param photos post photos
     * @return mapped photo responses
     */
    default List<PostPhotoResponseDto> mapPhotos(final List<PostPhoto> photos) {
        if (photos == null) {
            return Collections.emptyList();
        }
        return photos.stream()
                .map(this::mapPhoto)
                .toList();
    }

    /**
     * Maps post photo entity to response model.
     *
     * @param photo post photo entity
     * @return mapped photo response
     */
    default PostPhotoResponseDto mapPhoto(final PostPhoto photo) {
        if (photo == null) {
            return null;
        }
        return PostPhotoResponseDto.builder()
                .id(photo.getId())
                .url(photo.getUrl())
                .postId(photo.getPost() == null ? null : photo.getPost().getId())
                .build();
    }
}

package com.allterra.server.model.poi;

import com.allterra.server.model.poi.dto.PoiResponseDto;
import com.allterra.server.model.poi.dto.request.PoiCreateRequestDto;
import com.allterra.server.model.poi.dto.request.PoiUpdateRequestDto;
import com.allterra.server.photo.dto.PoiPhotoResponseDto;
import com.allterra.server.photo.model.PoiPhoto;
import com.allterra.server.model.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.Collections;
import java.util.List;

/**
 * Mapper for {@link Poi}.
 */
@Mapper(componentModel = "spring")
public interface PoiMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "users", ignore = true)
    @Mapping(target = "photos", ignore = true)
    Poi toEntity(final PoiCreateRequestDto poiCreateRequestDto);

    @Mapping(target = "userIds", source = "users")
    PoiResponseDto toDto(final Poi poi);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    void updateEntityFromDto(final PoiUpdateRequestDto poiUpdateRequestDto, final @MappingTarget Poi poi);

    /**
     * Maps poi users to list of user ids.
     *
     * @param users poi users
     * @return user ids
     */
    default List<java.util.UUID> mapUsers(final List<User> users) {
        if (users == null) {
            return Collections.emptyList();
        }
        return users.stream()
                .map(User::getId)
                .toList();
    }

    /**
     * Maps poi photo entities to response models.
     *
     * @param photos poi photos
     * @return mapped photo responses
     */
    default List<PoiPhotoResponseDto> mapPhotos(final List<PoiPhoto> photos) {
        if (photos == null) {
            return Collections.emptyList();
        }
        return photos.stream()
                .map(this::mapPhoto)
                .toList();
    }

    /**
     * Maps poi photo entity to response model.
     *
     * @param photo poi photo entity
     * @return mapped photo response
     */
    default PoiPhotoResponseDto mapPhoto(final PoiPhoto photo) {
        if (photo == null) {
            return null;
        }
        return PoiPhotoResponseDto.builder()
                .id(photo.getId())
                .url(photo.getUrl())
                .poiId(photo.getPoi() == null ? null : photo.getPoi().getId())
                .build();
    }
}

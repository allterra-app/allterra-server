package com.allterra.server.photo.mapper;

import com.allterra.server.photo.dto.PoiPhotoResponseDto;
import com.allterra.server.photo.dto.request.create.PoiPhotoCreateRequestDto;
import com.allterra.server.photo.dto.request.update.PoiPhotoUpdateRequestDto;
import com.allterra.server.photo.model.PoiPhoto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * {@link PhotoMapper} implementation for {@link PoiPhoto}.
 */
@Mapper(componentModel = "spring")
public interface PoiPhotoMapper extends PhotoMapper<PoiPhoto, PoiPhotoResponseDto, PoiPhotoCreateRequestDto, PoiPhotoUpdateRequestDto> {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "poi", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    PoiPhoto toEntity(final PoiPhotoCreateRequestDto createRequestDto);

    @Mapping(target = "poiId", source = "poi.id")
    PoiPhotoResponseDto toDto(final PoiPhoto entity);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    void updateEntityFromDto(final PoiPhotoUpdateRequestDto updateRequestDto, final @MappingTarget PoiPhoto entity);
}

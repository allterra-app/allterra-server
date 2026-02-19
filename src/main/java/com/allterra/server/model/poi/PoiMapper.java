package com.allterra.server.model.poi;

import com.allterra.server.model.poi.dto.PoiResponseDto;
import com.allterra.server.model.poi.dto.request.PoiCreateRequestDto;
import com.allterra.server.model.poi.dto.request.PoiUpdateRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Mapper for {@link Poi}.
 */
@Mapper(componentModel = "spring")
public interface PoiMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "users", ignore = true)
    Poi toEntity(final PoiCreateRequestDto poiCreateRequestDto);

    PoiResponseDto toDto(final Poi poi);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    void updateEntityFromDto(final PoiUpdateRequestDto poiUpdateRequestDto, final @MappingTarget Poi poi);
}

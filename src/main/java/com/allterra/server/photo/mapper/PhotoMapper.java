package com.allterra.server.photo.mapper;

import com.allterra.server.photo.model.Photo;
import org.mapstruct.MappingTarget;

/**
 * Mapper for {@link Photo}.
 *
 * @param <T> type of {@link Photo}
 * @param <DTO> response DTO
 * @param <CreateDTO> create request DTO
 * @param <UpdateDTO> update request DTO
 */
public interface PhotoMapper<T extends Photo, DTO, CreateDTO, UpdateDTO> {

    T toEntity(final CreateDTO createRequestDto);

    DTO toDto(final T entity);

    void updateEntityFromDto(final UpdateDTO updateRequestDto, final @MappingTarget T entity);
}

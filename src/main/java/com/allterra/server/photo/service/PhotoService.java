package com.allterra.server.photo.service;

import com.allterra.server.exception.ResourceNotFoundException;
import com.allterra.server.photo.mapper.PhotoMapper;
import com.allterra.server.photo.model.Photo;
import com.allterra.server.photo.repository.PhotoRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Base {@link Photo} service.
 *
 * @param <T> type of photo
 * @param <DTO> DTO model for photo type
 * @param <CreateDTO> create DTO model for photo type
 * @param <UpdateDTO> update DTO model for photo type
 */
@RequiredArgsConstructor
public abstract class PhotoService<T extends Photo, DTO, CreateDTO, UpdateDTO> {
    protected final PhotoRepository<T> repository;
    protected final PhotoMapper<T, DTO, CreateDTO, UpdateDTO> mapper;

    /**
     * Creates Photo.
     *
     * @param dto create DTO model
     * @return response DTO for created photo
     */
    public DTO create(final CreateDTO dto) {
        T entity = mapper.toEntity(dto);
        T saved = repository.save(entity);
        return mapper.toDto(saved);
    }

    /**
     * Gets photo by id.
     *
     * @param id photo id
     * @return response DTO for photo
     */
    public DTO get(final java.util.UUID id) {
        return repository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Photo not found with id: " + id));
    }

    /**
     * Gets all photos.
     *
     * @return response DTO for all photos
     */
    public List<DTO> getAll() {
        return repository.findAll().stream()
                .map(mapper::toDto)
                .toList();
    }

    /**
     * Updates photo.
     *
     * @param id photo id
     * @param dto update DTO
     * @return response DTO for updated photo
     */
    public DTO update(final java.util.UUID id, final UpdateDTO dto) {
        T existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Photo not found with id: " + id));
        mapper.updateEntityFromDto(dto, existing);
        T updated = repository.save(existing);
        return mapper.toDto(updated);
    }

    /**
     * Deletes photo.
     *
     * @param id photo id
     */
    public void delete(final java.util.UUID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Photo not found with id: " + id);
        }
        repository.deleteById(id);
    }
}

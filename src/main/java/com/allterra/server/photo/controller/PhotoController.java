package com.allterra.server.photo.controller;

import com.allterra.server.photo.dto.request.create.PhotoCreateRequestDto;
import com.allterra.server.photo.dto.request.update.PhotoUpdateRequestDto;
import com.allterra.server.photo.model.Photo;
import com.allterra.server.photo.service.PhotoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * RestController for {@link Photo}.
 *
 * @param <T> type of photo
 * @param <DTO> DTO model for photo type
 * @param <CreateDTO> create DTO model for photo type
 * @param <UpdateDTO> update DTO model for photo type
 */
@RequiredArgsConstructor
public abstract class PhotoController<T extends Photo, DTO, CreateDTO, UpdateDTO> {
    protected final PhotoService<T, DTO, CreateDTO, UpdateDTO> service;

    /**
     * Creates entity form {@link PhotoCreateRequestDto}.
     *
     * @param dto DTO model
     * @return {@link ResponseEntity} for created entity
     */
    @PostMapping
    public ResponseEntity<DTO> create(final @RequestBody @Valid CreateDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    /**
     * Gets entity form by id.
     *
     * @param id entity id
     * @return {@link ResponseEntity} for created entity
     */
    @GetMapping("/{id}")
    public ResponseEntity<DTO> get(final @PathVariable java.util.UUID id) {
        return ResponseEntity.ok(service.get(id));
    }

    /**
     * Gets all entities.
     *
     * @return ResponseEntity for all entities
     */
    @GetMapping
    public ResponseEntity<List<DTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    /**
     * Update entity form {@link PhotoUpdateRequestDto}.
     * @param id entity id
     * @param dto DTO update model
     * @return ResponseEntity for updated entity
     */
    @PutMapping("/{id}")
    public ResponseEntity<DTO> update(final @PathVariable java.util.UUID id, final @RequestBody @Valid UpdateDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    /**
     * Deletes entity.
     *
     * @param id entity id
     * @return ResponseEntity for entity deleting
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(final @PathVariable java.util.UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

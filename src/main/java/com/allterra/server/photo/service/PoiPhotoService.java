package com.allterra.server.photo.service;

import com.allterra.server.exception.ResourceNotFoundException;
import com.allterra.server.model.poi.PoiRepository;
import com.allterra.server.photo.dto.PoiPhotoResponseDto;
import com.allterra.server.photo.dto.request.create.PoiPhotoCreateRequestDto;
import com.allterra.server.photo.dto.request.update.PoiPhotoUpdateRequestDto;
import com.allterra.server.photo.mapper.PoiPhotoMapper;
import com.allterra.server.photo.model.PoiPhoto;
import com.allterra.server.photo.repository.PoiPhotoRepository;
import org.springframework.stereotype.Service;

/**
 * Service for {@link PoiPhoto}.
 */
@Service
public class PoiPhotoService extends PhotoService<PoiPhoto, PoiPhotoResponseDto, PoiPhotoCreateRequestDto, PoiPhotoUpdateRequestDto> {
    private final PoiRepository poiRepository;

    public PoiPhotoService(
            final PoiPhotoRepository repository,
            final PoiPhotoMapper mapper,
            final PoiRepository poiRepository
    ) {
        super(repository, mapper);
        this.poiRepository = poiRepository;
    }

    @Override
    public PoiPhotoResponseDto create(final PoiPhotoCreateRequestDto dto) {
        final var poiId = dto.getPoiId();
        if (poiId == null) {
            throw new IllegalArgumentException("poiId is required");
        }
        final var poi = poiRepository.findById(poiId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Poi with id %s not found", poiId)));

        final var entity = mapper.toEntity(dto);
        entity.setPoi(poi);
        final var saved = repository.save(entity);
        return mapper.toDto(saved);
    }
}

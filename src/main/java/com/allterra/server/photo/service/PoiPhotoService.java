package com.allterra.server.photo.service;

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
    public PoiPhotoService(final PoiPhotoRepository repository, final PoiPhotoMapper mapper) {
        super(repository, mapper);
    }
}

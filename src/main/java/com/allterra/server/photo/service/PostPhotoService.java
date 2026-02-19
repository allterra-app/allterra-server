package com.allterra.server.photo.service;

import com.allterra.server.photo.dto.PostPhotoResponseDto;
import com.allterra.server.photo.dto.request.create.PostPhotoCreateRequestDto;
import com.allterra.server.photo.dto.request.update.PostPhotoUpdateRequestDto;
import com.allterra.server.photo.mapper.PostPhotoMapper;
import com.allterra.server.photo.model.PostPhoto;
import com.allterra.server.photo.repository.PostPhotoRepository;
import org.springframework.stereotype.Service;

/**
 * Service for {@link PostPhoto}.
 */
@Service
public class PostPhotoService extends PhotoService<PostPhoto, PostPhotoResponseDto, PostPhotoCreateRequestDto, PostPhotoUpdateRequestDto> {
    public PostPhotoService(final PostPhotoRepository repository, final PostPhotoMapper mapper) {
        super(repository, mapper);
    }
}

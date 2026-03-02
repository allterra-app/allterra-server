package com.allterra.server.photo.service;

import com.allterra.server.exception.ResourceNotFoundException;
import com.allterra.server.model.post.PostRepository;
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
    private final PostRepository postRepository;

    public PostPhotoService(
            final PostPhotoRepository repository,
            final PostPhotoMapper mapper,
            final PostRepository postRepository
    ) {
        super(repository, mapper);
        this.postRepository = postRepository;
    }

    @Override
    public PostPhotoResponseDto create(final PostPhotoCreateRequestDto dto) {
        final var postId = dto.getPostId();
        if (postId == null) {
            throw new IllegalArgumentException("postId is required");
        }
        final var post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Post with id %s not found", postId)));

        final var entity = mapper.toEntity(dto);
        entity.setPost(post);
        final var saved = repository.save(entity);
        return mapper.toDto(saved);
    }
}

package com.allterra.server.model.post;

import com.allterra.server.model.post.dto.PostResponseDto;
import com.allterra.server.model.post.dto.request.PostCreateRequestDto;
import com.allterra.server.model.post.dto.request.PostUpdateRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Mapper for {@link Post}.
 */
@Mapper(componentModel = "spring")
public interface PostMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    Post toEntity(final PostCreateRequestDto postCreateRequestDto);

    PostResponseDto toDto(final Post post);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    void updateEntityFromDto(final PostUpdateRequestDto postUpdateRequestDto, final @MappingTarget Post post);
}

package com.allterra.server.model.post.dto;

import com.allterra.server.photo.dto.PostPhotoResponseDto;
import com.allterra.server.model.post.PostAudience;
import com.allterra.server.model.post.PostType;
import com.allterra.server.model.post.ActivityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Post response DTO model.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostResponseDto {

    private java.util.UUID id;

    private PostUserResponseDto user;

    private String title;

    private String body;

    private PostAudience audience;

    private PostType type;

    private ActivityType activity;

    private UUID tripId;

    private UUID routeId;

    private List<PostPhotoResponseDto> photos;

    private int likeCount;

    private boolean liked;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;
}

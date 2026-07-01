package com.allterra.server.model.post.dto.request;

import com.allterra.server.model.post.ActivityType;
import com.allterra.server.model.post.PostAudience;
import com.allterra.server.model.post.PostType;
import com.allterra.server.photo.model.PostPhoto;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Post request DTO to update model.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostUpdateRequestDto {

    @NotBlank(message = "Title cannot be empty")
    private String title;

    private String body;

    private PostAudience audience;

    private PostType type;

    private ActivityType activity;

    private UUID tripId;

    private UUID routeId;

    private List<PostPhoto> photos;
}

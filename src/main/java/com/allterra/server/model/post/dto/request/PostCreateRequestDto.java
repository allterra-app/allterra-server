package com.allterra.server.model.post.dto.request;

import com.allterra.server.model.post.ActivityType;
import com.allterra.server.model.post.PostAudience;
import com.allterra.server.model.post.PostType;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Post request DTO to create model.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostCreateRequestDto {

    private java.util.UUID userId;

    private java.util.UUID tripId;

    private java.util.UUID routeId;

    private PostAudience audience;

    private PostType type;

    private ActivityType activity;

    @NotBlank
    private String title;

    private String body;
}

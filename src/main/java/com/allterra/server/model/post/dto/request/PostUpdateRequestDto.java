package com.allterra.server.model.post.dto.request;

import com.allterra.server.model.user.User;
import com.allterra.server.photo.model.PostPhoto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * User request DTO to update model.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostUpdateRequestDto {

    @NotNull(message = "ID is required")
    private java.util.UUID id;

    @NotNull(message = "User must be defined")
    private User user;

    @NotBlank(message = "Title cannot be empty")
    private String title;

    private String body;

    private List<PostPhoto> photos;
}

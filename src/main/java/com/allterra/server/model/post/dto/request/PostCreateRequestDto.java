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
 * Post request DTO to create model.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostCreateRequestDto {

    @NotNull
    private User user;

    @NotBlank
    private String title;

    private String body;

    private List<PostPhoto> photos;
}

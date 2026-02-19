package com.allterra.server.model.post.dto;

import com.allterra.server.model.user.User;
import com.allterra.server.photo.model.PostPhoto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Post response DTO model.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostResponseDto {

    private java.util.UUID id;

    private User user;

    private String title;

    private String body;

    private List<PostPhoto> photos;
}

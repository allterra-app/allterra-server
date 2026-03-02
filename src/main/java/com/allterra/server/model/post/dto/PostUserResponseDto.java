package com.allterra.server.model.post.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Post user reference response dto.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostUserResponseDto {
    private java.util.UUID id;
    private String username;
    private String email;
}

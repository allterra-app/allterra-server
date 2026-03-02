package com.allterra.server.photo.dto;

import com.allterra.server.photo.model.PostPhoto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Response DTO for {@link PostPhoto}.
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PostPhotoResponseDto extends PhotoResponseDto {
    private java.util.UUID postId;
}

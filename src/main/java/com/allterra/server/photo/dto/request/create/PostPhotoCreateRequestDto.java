package com.allterra.server.photo.dto.request.create;

import com.allterra.server.photo.model.PostPhoto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Create request DTO for {@link PostPhoto}.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PostPhotoCreateRequestDto extends PhotoCreateRequestDto {
    private java.util.UUID postId;
}

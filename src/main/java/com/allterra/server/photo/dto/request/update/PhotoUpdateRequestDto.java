package com.allterra.server.photo.dto.request.update;

import com.allterra.server.photo.model.Photo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Update request DTO for {@link Photo}.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PhotoUpdateRequestDto {
    private java.util.UUID id;
    private String url;
}

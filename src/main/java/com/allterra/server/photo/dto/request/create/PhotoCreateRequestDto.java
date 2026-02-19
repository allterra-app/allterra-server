package com.allterra.server.photo.dto.request.create;

import com.allterra.server.photo.model.Photo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Create request DTO for {@link Photo}.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PhotoCreateRequestDto {
    private String url;
}

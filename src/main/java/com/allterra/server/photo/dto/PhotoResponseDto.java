package com.allterra.server.photo.dto;

import com.allterra.server.photo.model.Photo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Response DTO for {@link Photo}.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PhotoResponseDto {
    private java.util.UUID id;
    private String url;
}

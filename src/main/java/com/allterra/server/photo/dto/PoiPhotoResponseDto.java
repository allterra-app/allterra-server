package com.allterra.server.photo.dto;

import com.allterra.server.photo.model.PoiPhoto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Response DTO for {@link PoiPhoto}.
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PoiPhotoResponseDto extends PhotoResponseDto {
    private java.util.UUID poiId;
}

package com.allterra.server.photo.dto.request.create;

import com.allterra.server.photo.model.PoiPhoto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Create request DTO for {@link PoiPhoto}.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PoiPhotoCreateRequestDto extends PhotoCreateRequestDto {
    private java.util.UUID poiId;
}

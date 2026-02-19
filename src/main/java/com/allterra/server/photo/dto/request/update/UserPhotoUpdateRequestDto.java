package com.allterra.server.photo.dto.request.update;

import com.allterra.server.model.user.User;
import com.allterra.server.photo.model.PoiPhoto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Update request DTO for {@link PoiPhoto}.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserPhotoUpdateRequestDto extends PhotoUpdateRequestDto {
    private User user;
}

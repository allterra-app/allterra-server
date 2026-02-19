package com.allterra.server.photo.dto.request.create;

import com.allterra.server.model.user.User;
import com.allterra.server.photo.model.UserPhoto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Create request DTO for {@link UserPhoto}.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserPhotoCreateRequestDto extends PhotoCreateRequestDto {
    private User user;
}

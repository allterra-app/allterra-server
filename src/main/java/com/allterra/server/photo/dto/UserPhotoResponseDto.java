package com.allterra.server.photo.dto;

import com.allterra.server.model.user.User;
import com.allterra.server.photo.model.UserPhoto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Response DTO for {@link UserPhoto}.
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserPhotoResponseDto extends PhotoResponseDto {
    private User user;
}

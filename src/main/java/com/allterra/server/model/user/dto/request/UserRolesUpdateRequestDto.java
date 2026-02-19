package com.allterra.server.model.user.dto.request;

import com.allterra.server.model.user.UserRole;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * Request DTO for user roles update.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRolesUpdateRequestDto {

    @NotEmpty(message = "At least one role is required")
    private Set<UserRole> roles;
}

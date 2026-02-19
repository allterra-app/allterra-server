package com.allterra.server.model.user.dto.request;

import com.allterra.server.model.user.SubscriptionPlan;
import com.allterra.server.model.user.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * User request DTO to update model.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateRequestDto {

    @NotNull(message = "ID is required")
    private java.util.UUID id;

    private String username;

    private String password;

    @Email(message = "Email should be valid")
    private String email;

    private String firstName;

    private String lastName;

    @Pattern(regexp = "\\+?[0-9]{10,15}", message = "Invalid phone number")
    private String phoneNumber;

    @Pattern(regexp = "\\d{2}-\\d{2}-\\d{4}", message = "Birth date must be in dd-MM-yyyy format")
    private String birthDate;

    private String city;

    private String userPhoto;

    private SubscriptionPlan subscriptionPlan;

    private Set<UserRole> roles;
}

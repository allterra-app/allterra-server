package com.allterra.server.model.user.dto.request;

import com.allterra.server.model.poi.Poi;
import com.allterra.server.model.post.Post;
import com.allterra.server.photo.model.UserPhoto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * User request DTO to create model.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserCreateRequestDto {

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    private String firstName;

    private String lastName;

    @Pattern(regexp = "\\+?[0-9]{10,15}", message = "Invalid phone number")
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    @Pattern(regexp = "\\d{2}-\\d{2}-\\d{4}", message = "Birth date must be in dd-MM-yyyy format")
    private String birthDate;

    private String city;

    private UserPhoto userPhoto;

    private List<Post> posts;

    private List<Poi> pois;
}

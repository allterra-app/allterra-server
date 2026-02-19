package com.allterra.server.model.user.dto;

import com.allterra.server.model.user.SubscriptionPlan;
import com.allterra.server.model.user.UserRole;
import com.allterra.server.model.poi.Poi;
import com.allterra.server.model.post.Post;
import com.allterra.server.photo.model.UserPhoto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * User response DTO model.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto {

    private java.util.UUID id;

    private String username;

    private String email;

    private boolean emailVerified;

    private String firstName;

    private String lastName;

    private String phoneNumber;

    private String birthDate;

    private String city;

    private Set<UserRole> roles;

    private SubscriptionPlan subscriptionPlan;

    private LocalDateTime subscriptionStartedAt;

    private LocalDateTime subscriptionExpiresAt;

    private LocalDateTime deletedAt;

    private UserPhoto userPhoto;

    private List<Post> posts;

    private List<Poi> pois;
}

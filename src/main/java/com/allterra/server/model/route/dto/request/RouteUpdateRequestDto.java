package com.allterra.server.model.route.dto.request;

import com.allterra.server.model.user.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Route update request dto.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RouteUpdateRequestDto {
    @NotNull
    private java.util.UUID id;

    private User user;

    @NotBlank
    private String title;

    private String description;

    @NotBlank
    private String gpxContent;

    private Double distanceKm;

    private Long durationMinutes;

    private Integer pointCount;
}

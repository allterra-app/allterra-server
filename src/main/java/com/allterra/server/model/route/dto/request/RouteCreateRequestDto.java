package com.allterra.server.model.route.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Route create request dto.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RouteCreateRequestDto {
    private java.util.UUID userId;

    @NotBlank
    private String title;

    private String description;

    @NotBlank
    private String gpxContent;

    private Double distanceKm;

    private Long durationMinutes;

    private Integer pointCount;
}

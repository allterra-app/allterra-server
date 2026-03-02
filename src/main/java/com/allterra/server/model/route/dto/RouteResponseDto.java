package com.allterra.server.model.route.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Route response dto.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RouteResponseDto {
    private java.util.UUID id;
    private java.util.UUID userId;
    private String title;
    private String description;
    private String gpxContent;
    private Double distanceKm;
    private Long durationMinutes;
    private Integer pointCount;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}

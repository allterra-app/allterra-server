package com.allterra.server.model.route.dto.request;

import jakarta.validation.constraints.NotNull;
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

    private String title;

    private String description;

    @NotNull
    private java.util.UUID gpxFileId;
}

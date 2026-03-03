package com.allterra.server.model.route.dto.request;

import jakarta.validation.constraints.NotBlank;
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
    @NotBlank
    private String title;

    private String description;

    private java.util.UUID gpxFileId;
}

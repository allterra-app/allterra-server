package com.allterra.server.model.route.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Route preview point dto.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoutePointDto {
    private double lat;
    private double lon;
}

package com.allterra.server.model.route.gpx;

import com.allterra.server.model.route.dto.RoutePointDto;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Parsed GPX route data used for Route persistence.
 */
@Value
@Builder
public class ParsedRouteData {
    String routeName;
    Double distanceKm;
    Long durationMinutes;
    Integer pointCount;
    LocalDateTime startedAt;
    List<RoutePointDto> previewPoints;
}

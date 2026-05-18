package com.allterra.server.model.trip.dto;

import com.allterra.server.model.trip.TripActivity;
import com.allterra.server.model.trip.TripStatus;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Request DTO for creating or updating a trip.
 */
@Data
public final class TripRequestDto {
    private String title;
    private String region;
    private TripStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
    private TripActivity activity;
    private UUID routeId;
}

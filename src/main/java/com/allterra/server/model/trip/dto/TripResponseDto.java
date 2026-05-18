package com.allterra.server.model.trip.dto;

import com.allterra.server.model.trip.TripActivity;
import com.allterra.server.model.trip.TripStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for a trip.
 */
@Data
@Builder
public final class TripResponseDto {
    private UUID id;
    private String title;
    private String region;
    private TripStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
    private TripActivity activity;
    private UUID routeId;
    private UUID userId;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}

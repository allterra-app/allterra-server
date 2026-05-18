package com.allterra.server.model.gear.dto;

import com.allterra.server.model.gear.GearStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for gear item.
 */
@Getter
@Builder
public final class GearResponseDto {
    private UUID id;
    private String name;
    private String category;
    private BigDecimal weightKg;
    private GearStatus status;
    private UUID userId;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}

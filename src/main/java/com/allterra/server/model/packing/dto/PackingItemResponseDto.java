package com.allterra.server.model.packing.dto;

import com.allterra.server.model.gear.GearStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO representing a gear item in the context of a trip's packing list.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PackingItemResponseDto {
    private UUID gearId;
    private String name;
    private String category;
    private BigDecimal weightKg;
    private GearStatus status;
    private boolean packed;
}

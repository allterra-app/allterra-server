package com.allterra.server.model.gear.dto;

import com.allterra.server.model.gear.GearStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Request DTO for creating/updating gear item.
 */
@Getter
@Setter
public final class GearRequestDto {

    @NotBlank
    private String name;

    @NotBlank
    private String category;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal weightKg;

    @NotNull
    private GearStatus status;
}

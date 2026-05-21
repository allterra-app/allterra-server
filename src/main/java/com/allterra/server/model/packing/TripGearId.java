package com.allterra.server.model.packing;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

/**
 * Composite primary key for TripGear entity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TripGearId implements Serializable {
    private UUID tripId;
    private UUID gearId;
}

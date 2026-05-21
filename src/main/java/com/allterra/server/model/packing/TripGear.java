package com.allterra.server.model.packing;

import com.allterra.server.model.gear.GearItem;
import com.allterra.server.model.trip.Trip;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Represents the association between a trip and a gear item, including packed status.
 */
@Entity
@Table(name = "trip_gear")
@IdClass(TripGearId.class)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class TripGear {

    @Id
    @Column(name = "trip_id")
    private UUID tripId;

    @Id
    @Column(name = "gear_id")
    private UUID gearId;

    @Column(name = "is_packed", nullable = false)
    private boolean packed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", insertable = false, updatable = false)
    private Trip trip;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gear_id", insertable = false, updatable = false)
    private GearItem gear;
}

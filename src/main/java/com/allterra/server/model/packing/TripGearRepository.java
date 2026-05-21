package com.allterra.server.model.packing;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for TripGear association.
 */
@Repository
public interface TripGearRepository extends JpaRepository<TripGear, TripGearId> {

    /**
     * Finds all gear associations for a specific trip.
     * @param tripId the trip ID
     * @return list of associations
     */
    List<TripGear> findAllByTripId(UUID tripId);

    /**
     * Finds a specific association by trip and gear IDs.
     * @param tripId the trip ID
     * @param gearId the gear ID
     * @return the association if found
     */
    Optional<TripGear> findByTripIdAndGearId(UUID tripId, UUID gearId);
}

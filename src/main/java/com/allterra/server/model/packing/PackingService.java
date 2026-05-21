package com.allterra.server.model.packing;

import com.allterra.server.model.gear.GearItem;
import com.allterra.server.model.gear.GearItemRepository;
import com.allterra.server.model.packing.dto.PackingItemResponseDto;
import com.allterra.server.model.trip.Trip;
import com.allterra.server.model.trip.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for managing packing lists for trips.
 */
@Service
@RequiredArgsConstructor
public class PackingService {

    private final TripGearRepository tripGearRepository;
    private final TripRepository tripRepository;
    private final GearItemRepository gearItemRepository;

    /**
     * Retrieves the packing list for a trip.
     *
     * @param tripId the trip ID
     * @param userId the owner ID
     * @return list of packing items
     */
    @Transactional(readOnly = true)
    public List<PackingItemResponseDto> getPackingList(final UUID tripId, final UUID userId) {
        verifyTripOwnership(tripId, userId);
        return tripGearRepository.findAllByTripId(tripId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /**
     * Toggles the packed status of a gear item in a trip.
     *
     * @param tripId the trip ID
     * @param gearId the gear ID
     * @param packed the new status
     * @param userId the owner ID
     * @return updated packing item
     */
    @Transactional
    public PackingItemResponseDto updatePackingStatus(
            final UUID tripId,
            final UUID gearId,
            final boolean packed,
            final UUID userId
    ) {
        verifyTripOwnership(tripId, userId);
        TripGear tripGear = tripGearRepository.findByTripIdAndGearId(tripId, gearId)
                .orElseThrow(() -> new RuntimeException("Gear item not found in this trip"));

        tripGear.setPacked(packed);
        return mapToDto(tripGearRepository.save(tripGear));
    }

    /**
     * Adds a gear item to the packing list of a trip.
     *
     * @param tripId the trip ID
     * @param gearId the gear ID
     * @param userId the owner ID
     * @return the added packing item
     */
    @Transactional
    public PackingItemResponseDto addGearToTrip(final UUID tripId, final UUID gearId, final UUID userId) {
        verifyTripOwnership(tripId, userId);
        verifyGearOwnership(gearId, userId);

        if (tripGearRepository.existsById(new TripGearId(tripId, gearId))) {
            throw new RuntimeException("Gear item already added to this trip");
        }

        TripGear tripGear = TripGear.builder()
                .tripId(tripId)
                .gearId(gearId)
                .packed(false)
                .build();

        return mapToDto(tripGearRepository.save(tripGear));
    }

    /**
     * Removes a gear item from the packing list of a trip.
     *
     * @param tripId the trip ID
     * @param gearId the gear ID
     * @param userId the owner ID
     */
    @Transactional
    public void removeGearFromTrip(final UUID tripId, final UUID gearId, final UUID userId) {
        verifyTripOwnership(tripId, userId);
        TripGear tripGear = tripGearRepository.findByTripIdAndGearId(tripId, gearId)
                .orElseThrow(() -> new RuntimeException("Gear item not found in this trip"));

        tripGearRepository.delete(tripGear);
    }

    private void verifyTripOwnership(final UUID tripId, final UUID userId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found"));
        if (!trip.getUser().getId().equals(userId)) {
            throw new RuntimeException("User does not own this trip");
        }
    }

    private void verifyGearOwnership(final UUID gearId, final UUID userId) {
        GearItem gear = gearItemRepository.findById(gearId)
                .orElseThrow(() -> new RuntimeException("Gear item not found"));
        if (!gear.getUser().getId().equals(userId)) {
            throw new RuntimeException("User does not own this gear item");
        }
    }

    private PackingItemResponseDto mapToDto(final TripGear tripGear) {
        GearItem gear = tripGear.getGear();
        return PackingItemResponseDto.builder()
                .gearId(gear.getId())
                .name(gear.getName())
                .category(gear.getCategory())
                .weightKg(gear.getWeightKg())
                .status(gear.getStatus())
                .packed(tripGear.isPacked())
                .build();
    }
}

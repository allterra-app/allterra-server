package com.allterra.server.model.packing;

import com.allterra.server.model.packing.dto.PackingItemResponseDto;
import com.allterra.server.model.packing.dto.PackingStatusUpdateDto;
import com.allterra.server.model.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * Controller for managing trip packing lists.
 */
@RestController
@RequestMapping("/trips/{tripId}/gear")
@RequiredArgsConstructor
public class PackingController {

    private final PackingService packingService;
    private final UserRepository userRepository;

    /**
     * Gets the packing list for a trip.
     * @param tripId the trip ID
     * @param authentication auth principal
     * @return list of packing items
     */
    @GetMapping
    public List<PackingItemResponseDto> getPackingList(
            @PathVariable final UUID tripId,
            final Authentication authentication
    ) {
        UUID userId = getUserId(authentication);
        return packingService.getPackingList(tripId, userId);
    }

    /**
     * Adds a gear item to the packing list.
     * @param tripId the trip ID
     * @param gearId the gear ID
     * @param authentication auth principal
     * @return added item
     */
    @PostMapping("/{gearId}")
    public PackingItemResponseDto addGearToTrip(
            @PathVariable final UUID tripId,
            @PathVariable final UUID gearId,
            final Authentication authentication
    ) {
        UUID userId = getUserId(authentication);
        return packingService.addGearToTrip(tripId, gearId, userId);
    }

    /**
     * Updates the packed status of an item.
     * @param tripId the trip ID
     * @param gearId the gear ID
     * @param request update payload
     * @param authentication auth principal
     * @return updated item
     */
    @PutMapping("/{gearId}/packed")
    public PackingItemResponseDto updatePackingStatus(
            @PathVariable final UUID tripId,
            @PathVariable final UUID gearId,
            @RequestBody final PackingStatusUpdateDto request,
            final Authentication authentication
    ) {
        UUID userId = getUserId(authentication);
        return packingService.updatePackingStatus(tripId, gearId, request.isPacked(), userId);
    }

    /**
     * Removes an item from the packing list.
     * @param tripId the trip ID
     * @param gearId the gear ID
     * @param authentication auth principal
     * @return no content
     */
    @DeleteMapping("/{gearId}")
    public ResponseEntity<Void> removeGearFromTrip(
            @PathVariable final UUID tripId,
            @PathVariable final UUID gearId,
            final Authentication authentication
    ) {
        UUID userId = getUserId(authentication);
        packingService.removeGearFromTrip(tripId, gearId, userId);
        return ResponseEntity.noContent().build();
    }

    private UUID getUserId(final Authentication authentication) {
        return userRepository.findByEmailIgnoreCase(authentication.getName()).orElseThrow().getId();
    }
}

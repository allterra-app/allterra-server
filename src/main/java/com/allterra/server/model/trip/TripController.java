package com.allterra.server.model.trip;

import com.allterra.server.model.trip.dto.TripRequestDto;
import com.allterra.server.model.trip.dto.TripResponseDto;
import com.allterra.server.model.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * Controller for managing trips.
 */
@RestController
@RequestMapping("/trips")
@RequiredArgsConstructor
public final class TripController {

    private final TripService tripService;
    private final UserRepository userRepository;

    /**
     * Gets all trips for the authenticated user.
     * @param authentication authenticated user
     * @return list of trips
     */
    @GetMapping
    public List<TripResponseDto> getMyTrips(final Authentication authentication) {
        UUID userId = userRepository.findByEmailIgnoreCase(authentication.getName()).orElseThrow().getId();
        return tripService.getUserTrips(userId);
    }

    /**
     * Gets a specific trip.
     * @param id trip id
     * @return trip details
     */
    @GetMapping("/{id}")
    public TripResponseDto getTrip(@PathVariable final UUID id) {
        return tripService.getTrip(id);
    }

    /**
     * Creates a new trip.
     * @param authentication authenticated user
     * @param request trip details
     * @return created trip
     */
    @PostMapping
    public TripResponseDto createTrip(final Authentication authentication, @RequestBody final TripRequestDto request) {
        UUID userId = userRepository.findByEmailIgnoreCase(authentication.getName()).orElseThrow().getId();
        return tripService.createTrip(userId, request);
    }

    /**
     * Deletes a trip.
     * @param id trip id
     * @return response
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrip(@PathVariable final UUID id) {
        tripService.deleteTrip(id);
        return ResponseEntity.noContent().build();
    }
}

package com.allterra.server.model.trip;

import com.allterra.server.model.trip.dto.TripRequestDto;
import com.allterra.server.model.trip.dto.TripResponseDto;
import com.allterra.server.model.user.User;
import com.allterra.server.model.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for managing trips.
 */
@Service
@RequiredArgsConstructor
public class TripService {

    private final TripRepository tripRepository;
    private final UserRepository userRepository;

    /**
     * Retrieves all trips for a user.
     * @param userId user id
     * @return list of trips
     */
    @Transactional(readOnly = true)
    public List<TripResponseDto> getUserTrips(final UUID userId) {
        return tripRepository.findAllByUserId(userId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /**
     * Gets a trip by id.
     * @param id trip id
     * @return trip dto
     */
    @Transactional(readOnly = true)
    public TripResponseDto getTrip(final UUID id) {
        return tripRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new RuntimeException("Trip not found"));
    }

    /**
     * Creates a new trip.
     * @param userId owner id
     * @param request trip details
     * @return created trip dto
     */
    @Transactional
    public TripResponseDto createTrip(final UUID userId, final TripRequestDto request) {
        User user = userRepository.findById(userId).orElseThrow();
        Trip trip = Trip.builder()
                .title(request.getTitle())
                .region(request.getRegion())
                .status(request.getStatus())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .activity(request.getActivity())
                .routeId(request.getRouteId())
                .user(user)
                .build();
        
        return mapToDto(tripRepository.save(trip));
    }

    /**
     * Deletes a trip.
     * @param id trip id
     */
    @Transactional
    public void deleteTrip(final UUID id) {
        tripRepository.deleteById(id);
    }

    private TripResponseDto mapToDto(final Trip trip) {
        return TripResponseDto.builder()
                .id(trip.getId())
                .title(trip.getTitle())
                .region(trip.getRegion())
                .status(trip.getStatus())
                .startDate(trip.getStartDate())
                .endDate(trip.getEndDate())
                .activity(trip.getActivity())
                .routeId(trip.getRouteId())
                .userId(trip.getUser().getId())
                .createdAt(trip.getCreatedAt())
                .modifiedAt(trip.getModifiedAt())
                .build();
    }
}

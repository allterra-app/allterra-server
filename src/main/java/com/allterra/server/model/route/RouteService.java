package com.allterra.server.model.route;

import com.allterra.server.exception.ResourceNotFoundException;
import com.allterra.server.model.route.dto.RouteResponseDto;
import com.allterra.server.model.route.dto.request.RouteCreateRequestDto;
import com.allterra.server.model.route.dto.request.RouteUpdateRequestDto;
import com.allterra.server.model.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for {@link Route}.
 */
@Service
@RequiredArgsConstructor
public class RouteService {
    private final RouteRepository routeRepository;
    private final RouteMapper routeMapper;
    private final UserRepository userRepository;

    /**
     * Creates route.
     *
     * @param requestDto route payload
     * @return created route
     */
    public RouteResponseDto create(final RouteCreateRequestDto requestDto) {
        final var entity = routeMapper.toEntity(requestDto);
        final var userId = requestDto.getUserId();
        if (userId == null) {
            throw new IllegalArgumentException("userId is required for route creation");
        }
        final var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("User with id %s not found", userId)));
        entity.setUser(user);
        return routeMapper.toDto(routeRepository.save(entity));
    }

    /**
     * Creates route for user.
     *
     * @param userId user id
     * @param requestDto route payload
     * @return created route
     */
    public RouteResponseDto createForUser(final java.util.UUID userId, final RouteCreateRequestDto requestDto) {
        final var entity = routeMapper.toEntity(requestDto);
        final var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("User with id %s not found", userId)));
        entity.setUser(user);
        return routeMapper.toDto(routeRepository.save(entity));
    }

    /**
     * Gets route by id.
     *
     * @param id route id
     * @return route response
     */
    public RouteResponseDto get(final java.util.UUID id) {
        return routeRepository.findById(id)
                .map(routeMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Route with id %s not found", id)));
    }

    /**
     * Gets all routes.
     *
     * @return route responses
     */
    public List<RouteResponseDto> getAll() {
        return routeRepository.findAll().stream().map(routeMapper::toDto).toList();
    }

    /**
     * Gets all routes for user.
     *
     * @param userId user id
     * @return route responses
     */
    public List<RouteResponseDto> getAllForUser(final java.util.UUID userId) {
        return routeRepository.findAllByUser_IdOrderByCreatedAtDesc(userId).stream().map(routeMapper::toDto).toList();
    }

    /**
     * Updates route.
     *
     * @param id route id
     * @param requestDto payload
     * @return updated route
     */
    public RouteResponseDto update(final java.util.UUID id, final RouteUpdateRequestDto requestDto) {
        return routeRepository.findById(id)
                .map(route -> {
                    routeMapper.updateEntityFromDto(requestDto, route);
                    return routeMapper.toDto(routeRepository.save(route));
                })
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Route with id %s not found", id)));
    }

    /**
     * Deletes route.
     *
     * @param id route id
     */
    public void delete(final java.util.UUID id) {
        final var route = routeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Route with id %s not found", id)));
        routeRepository.delete(route);
    }
}

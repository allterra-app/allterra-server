package com.allterra.server.model.route;

import com.allterra.server.exception.ResourceNotFoundException;
import com.allterra.server.media.service.MediaFileService;
import com.allterra.server.model.route.dto.RoutePointDto;
import com.allterra.server.model.route.dto.RouteResponseDto;
import com.allterra.server.model.route.dto.request.RouteCreateRequestDto;
import com.allterra.server.model.route.dto.request.RouteUpdateRequestDto;
import com.allterra.server.model.route.gpx.GpxRouteParserService;
import com.allterra.server.model.route.gpx.ParsedRouteData;
import com.allterra.server.model.user.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service for {@link Route}.
 */
@Service
@RequiredArgsConstructor
public class RouteService {
    private static final TypeReference<List<RoutePointDto>> ROUTE_POINTS_LIST_TYPE = new TypeReference<>() { };

    private final RouteRepository routeRepository;
    private final UserRepository userRepository;
    private final MediaFileService mediaFileService;
    private final GpxRouteParserService gpxRouteParserService;
    private final ObjectMapper objectMapper;

    /**
     * Creates route.
     *
     * @param requestDto route payload
     * @param requesterEmail authenticated user email
     * @param requesterAdmin whether requester has admin role
     * @return created route
     */
    public RouteResponseDto create(
            final RouteCreateRequestDto requestDto,
            final String requesterEmail,
            final boolean requesterAdmin
    ) {
        final var userId = requestDto.getUserId();
        if (userId == null) {
            throw new IllegalArgumentException("userId is required for route creation");
        }
        return createForUser(userId, requestDto, requesterEmail, requesterAdmin);
    }

    /**
     * Creates route for user.
     *
     * @param userId user id
     * @param requestDto route payload
     * @param requesterEmail authenticated user email
     * @param requesterAdmin whether requester has admin role
     * @return created route
     */
    public RouteResponseDto createForUser(
            final UUID userId,
            final RouteCreateRequestDto requestDto,
            final String requesterEmail,
            final boolean requesterAdmin
    ) {
        final var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("User with id %s not found", userId)));
        if (!requesterAdmin && (requesterEmail == null || !user.getEmail().equalsIgnoreCase(requesterEmail))) {
            throw new AccessDeniedException("Route creation is denied for target user");
        }

        final var gpxFileId = requestDto.getGpxFileId();
        if (gpxFileId == null) {
            throw new IllegalArgumentException("gpxFileId is required for route creation");
        }

        final var media = mediaFileService.getByIdForViewer(gpxFileId, requesterEmail, requesterAdmin);
        validateGpxFile(media.getFileName(), media.getContentType());

        final ParsedRouteData parsed = gpxRouteParserService.parse(media.getContent());

        final var entity = new Route();
        entity.setUser(user);
        entity.setTitle(resolveTitle(requestDto.getTitle(), parsed.getRouteName(), media.getFileName()));
        entity.setDescription(normalizeDescription(requestDto.getDescription()));
        applyParsedData(entity, parsed);
        entity.setGpxFileId(media.getId());
        entity.setGpxFileName(media.getFileName());
        entity.setGpxContentType(media.getContentType());

        return toDto(routeRepository.save(entity));
    }

    /**
     * Gets route by id.
     *
     * @param id route id
     * @return route response
     */
    @Transactional(readOnly = true)
    public RouteResponseDto get(final UUID id) {
        return routeRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Route with id %s not found", id)));
    }

    /**
     * Gets all routes.
     *
     * @return route responses
     */
    @Transactional(readOnly = true)
    public List<RouteResponseDto> getAll() {
        return routeRepository.findAll().stream().map(this::toDto).toList();
    }

    /**
     * Gets all routes for user.
     *
     * @param userId user id
     * @return route responses
     */
    @Transactional(readOnly = true)
    public List<RouteResponseDto> getAllForUser(final UUID userId) {
        return routeRepository.findAllByUser_IdOrderByCreatedAtDesc(userId).stream().map(this::toDto).toList();
    }

    /**
     * Updates route.
     *
     * @param id route id
     * @param requestDto payload
     * @param requesterEmail authenticated user email
     * @param requesterAdmin whether requester has admin role
     * @return updated route
     */
    public RouteResponseDto update(
            final UUID id,
            final RouteUpdateRequestDto requestDto,
            final String requesterEmail,
            final boolean requesterAdmin
    ) {
        final var route = routeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Route with id %s not found", id)));

        route.setTitle(requestDto.getTitle().trim());
        route.setDescription(normalizeDescription(requestDto.getDescription()));

        final var requestedFileId = requestDto.getGpxFileId();
        if (requestedFileId != null && !requestedFileId.equals(route.getGpxFileId())) {
            final var media = mediaFileService.getByIdForViewer(requestedFileId, requesterEmail, requesterAdmin);
            validateGpxFile(media.getFileName(), media.getContentType());

            final var parsed = gpxRouteParserService.parse(media.getContent());
            applyParsedData(route, parsed);
            route.setGpxFileId(media.getId());
            route.setGpxFileName(media.getFileName());
            route.setGpxContentType(media.getContentType());
        }

        return toDto(routeRepository.save(route));
    }

    /**
     * Deletes route.
     *
     * @param id route id
     */
    public void delete(final UUID id) {
        final var route = routeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Route with id %s not found", id)));
        routeRepository.delete(route);
    }

    /**
     * Deletes route for user.
     *
     * @param userId user id
     * @param routeId route id
     */
    public void deleteForUser(final UUID userId, final UUID routeId) {
        final var route = routeRepository.findById(routeId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Route with id %s not found", routeId)));
        final var routeUserId = route.getUser() == null ? null : route.getUser().getId();
        if (routeUserId == null || !routeUserId.equals(userId)) {
            throw new ResourceNotFoundException(
                    String.format("Route with id %s not found for user %s", routeId, userId)
            );
        }
        routeRepository.delete(route);
    }

    private void validateGpxFile(final String fileName, final String contentType) {
        final boolean looksLikeGpxByName = fileName != null && fileName.toLowerCase().endsWith(".gpx");
        final boolean looksLikeGpxByType = contentType != null
                && (contentType.equalsIgnoreCase("application/gpx+xml")
                || contentType.equalsIgnoreCase("application/xml")
                || contentType.equalsIgnoreCase("text/xml")
                || contentType.toLowerCase().contains("gpx"));

        if (!looksLikeGpxByName && !looksLikeGpxByType) {
            throw new IllegalArgumentException("Only GPX files are supported");
        }
    }

    private void applyParsedData(final Route route, final ParsedRouteData parsed) {
        route.setDistanceKm(parsed.getDistanceKm());
        route.setDurationMinutes(parsed.getDurationMinutes());
        route.setPointCount(parsed.getPointCount());
        route.setStartedAt(parsed.getStartedAt());
        route.setPreviewPointsJson(writePointsJson(parsed.getPreviewPoints()));
    }

    private String resolveTitle(final String requestedTitle, final String parsedRouteName, final String fileName) {
        if (requestedTitle != null && !requestedTitle.isBlank()) {
            return requestedTitle.trim();
        }

        if (parsedRouteName != null && !parsedRouteName.isBlank()) {
            return parsedRouteName.trim();
        }

        if (fileName != null && !fileName.isBlank()) {
            final int dotIndex = fileName.lastIndexOf('.');
            final String stripped = dotIndex > 0 ? fileName.substring(0, dotIndex) : fileName;
            if (!stripped.isBlank()) {
                return stripped.trim();
            }
        }

        return "Route";
    }

    private String normalizeDescription(final String description) {
        if (description == null) {
            return null;
        }
        final String trimmed = description.trim();
        return trimmed.isBlank() ? null : trimmed;
    }

    private String writePointsJson(final List<RoutePointDto> points) {
        try {
            return objectMapper.writeValueAsString(points == null ? List.of() : points);
        } catch (Exception exception) {
            throw new IllegalStateException("Cannot serialize route preview points", exception);
        }
    }

    private List<RoutePointDto> readPointsJson(final String pointsJson) {
        if (pointsJson == null || pointsJson.isBlank()) {
            return List.of();
        }

        try {
            return objectMapper.readValue(pointsJson, ROUTE_POINTS_LIST_TYPE);
        } catch (Exception exception) {
            return List.of();
        }
    }

    private RouteResponseDto toDto(final Route route) {
        final String normalizedTitle = route.getTitle() == null || route.getTitle().isBlank()
                ? "Route"
                : route.getTitle().trim();

        return RouteResponseDto.builder()
                .id(route.getId())
                .userId(route.getUser() == null ? null : route.getUser().getId())
                .title(normalizedTitle)
                .description(route.getDescription())
                .gpxFileId(route.getGpxFileId())
                .gpxFileName(route.getGpxFileName())
                .gpxContentType(route.getGpxContentType())
                .gpxFileUrl(route.getGpxFileId() == null ? null : "/api/v1/files/" + route.getGpxFileId())
                .distanceKm(route.getDistanceKm())
                .durationMinutes(route.getDurationMinutes())
                .pointCount(route.getPointCount())
                .startedAt(route.getStartedAt())
                .previewPoints(readPointsJson(route.getPreviewPointsJson()))
                .createdAt(route.getCreatedAt())
                .modifiedAt(route.getModifiedAt())
                .build();
    }
}

package com.allterra.server.model.route;

import com.allterra.server.model.route.dto.RouteResponseDto;
import com.allterra.server.model.route.dto.request.RouteCreateRequestDto;
import com.allterra.server.model.route.dto.request.RouteUpdateRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

/**
 * Controller for {@link Route}.
 */
@Slf4j
@RestController
@RequestMapping("/routes")
@RequiredArgsConstructor()
public class RouteController {
    private final RouteService routeService;

    /**
     * Creates route.
     *
     * @param requestDto route payload
     * @return created route
     */
    @PostMapping
    public ResponseEntity<RouteResponseDto> create(
            final @RequestBody @Valid RouteCreateRequestDto requestDto,
            final Authentication authentication
    ) {
        log.info("Create route for user [{}]", requestDto.getUserId());
        return ResponseEntity.ok(routeService.create(requestDto, requireUserEmail(authentication), isAdmin(authentication)));
    }

    /**
     * Creates route for user.
     *
     * @param userId user id
     * @param requestDto route payload
     * @return created route
     */
    @PostMapping("/users/{userId}")
    public ResponseEntity<RouteResponseDto> createForUser(
            final @PathVariable java.util.UUID userId,
            final @RequestBody @Valid RouteCreateRequestDto requestDto,
            final Authentication authentication
    ) {
        log.info("Create route for user [{}]", userId);
        return ResponseEntity.ok(routeService.createForUser(
                userId,
                requestDto,
                requireUserEmail(authentication),
                isAdmin(authentication)
        ));
    }

    /**
     * Gets route by id.
     *
     * @param id route id
     * @return route response
     */
    @GetMapping("/{id}")
    @PreAuthorize("@routeAccessGuard.canAccessRouteById(#id, authentication)")
    public ResponseEntity<RouteResponseDto> get(final @PathVariable java.util.UUID id) {
        log.info("Get route by id [{}]", id);
        return ResponseEntity.ok(routeService.get(id));
    }

    /**
     * Gets all routes.
     *
     * @return route responses
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RouteResponseDto>> getAll() {
        log.info("Get all routes");
        return ResponseEntity.ok(routeService.getAll());
    }

    /**
     * Gets all routes for user.
     *
     * @param userId user id
     * @return route responses
     */
    @GetMapping("/users/{userId}")
    @PreAuthorize("@userAccessGuard.canAccessUserById(#userId, authentication)")
    public ResponseEntity<List<RouteResponseDto>> getAllForUser(final @PathVariable java.util.UUID userId) {
        log.info("Get routes for user [{}]", userId);
        return ResponseEntity.ok(routeService.getAllForUser(userId));
    }

    /**
     * Updates route.
     *
     * @param routeId route id
     * @param requestDto route payload
     * @return updated route
     */
    @PutMapping("/{routeId}")
    @PreAuthorize("@routeAccessGuard.canAccessRouteById(#routeId, authentication)")
    public ResponseEntity<RouteResponseDto> update(
            final @PathVariable java.util.UUID routeId,
            final @RequestBody @Valid RouteUpdateRequestDto requestDto,
            final Authentication authentication
    ) {
        log.info("Update route [{}]", routeId);
        return ResponseEntity.ok(routeService.update(
                routeId,
                requestDto,
                requireUserEmail(authentication),
                isAdmin(authentication)
        ));
    }

    /**
     * Deletes route.
     *
     * @param routeId route id
     * @return no content
     */
    @DeleteMapping("/{routeId}")
    @PreAuthorize("@routeAccessGuard.canAccessRouteById(#routeId, authentication)")
    public ResponseEntity<Void> delete(final @PathVariable java.util.UUID routeId) {
        log.info("Delete route [{}]", routeId);
        routeService.delete(routeId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Deletes route for user.
     *
     * @param userId user id
     * @param routeId route id
     * @return no content
     */
    @DeleteMapping("/users/{userId}/{routeId}")
    @PreAuthorize("@userAccessGuard.canAccessUserById(#userId, authentication)")
    public ResponseEntity<Void> deleteForUser(
            final @PathVariable java.util.UUID userId,
            final @PathVariable java.util.UUID routeId
    ) {
        log.info("Delete route [{}] for user [{}]", routeId, userId);
        routeService.deleteForUser(userId, routeId);
        return ResponseEntity.noContent().build();
    }

    private String requireUserEmail(final Authentication authentication) {
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            throw new IllegalArgumentException("Authenticated user is required");
        }
        return authentication.getName();
    }

    private boolean isAdmin(final Authentication authentication) {
        if (authentication == null || authentication.getAuthorities() == null) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
    }
}

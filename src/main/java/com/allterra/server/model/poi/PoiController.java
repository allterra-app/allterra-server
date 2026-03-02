package com.allterra.server.model.poi;

import com.allterra.server.model.poi.dto.PoiResponseDto;
import com.allterra.server.model.poi.dto.request.PoiCreateRequestDto;
import com.allterra.server.model.poi.dto.request.PoiUpdateRequestDto;
import com.allterra.server.model.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * RestController for {@link Poi}.
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/pois")
public class PoiController {

    private final PoiService poiService;

    /**
     * Creates new Poi.
     *
     * @param poiRequestDto request to create Poi
     * @return {@link PoiResponseDto} for created Poi
     */
    @PostMapping
    public ResponseEntity<PoiResponseDto> createPoi(final @RequestBody @Valid PoiCreateRequestDto poiRequestDto) {
        log.info("Creating poi: [{}]", poiRequestDto);
        return ResponseEntity.ok(poiService.createPoi(poiRequestDto));
    }

    /**
     * Creates new Poi fot the User.
     *
     * @param userId user id
     * @param poiRequestDto request to create Poi
     * @return {@link PoiResponseDto} for created Poi
     */
    @PostMapping("/users/{userId}")
    public ResponseEntity<PoiResponseDto> createPoiForUser(
            final @PathVariable java.util.UUID userId,
            final @RequestBody @Valid PoiCreateRequestDto poiRequestDto
    ) {
        if (userId == null) {
            log.info("User with id [{}] not found", userId);
            return ResponseEntity.notFound().build();
        }

        log.info("Creating poi: [{}] for User [{}]", poiRequestDto, userId);
        return ResponseEntity.ok(poiService.createPoiForUser(userId, poiRequestDto));
    }

    /**
     * Returns Poi by id.
     *
     * @param id pois id
     * @return {@link PoiResponseDto} for poi
     */
    @GetMapping("/{id}")
    public ResponseEntity<PoiResponseDto> getPoi(final @PathVariable java.util.UUID id) {
        log.info("Getting poi: [{}]", id);
        return ResponseEntity.ok(poiService.getPoi(id));
    }

    /**
     * Returns all {@link Poi}.
     *
     * @return {@link PoiResponseDto} for all pois
     */
    @GetMapping
    public ResponseEntity<List<PoiResponseDto>> getAllPois() {
        log.info("Getting all pois, count: [{}]", poiService.getAllPois().size());
        return ResponseEntity.ok(poiService.getAllPois());
    }

    /**
     * Gets poi for user by id.
     *
     * @param userId user id
     * @param poiId poi id
     * @return {@link PoiResponseDto} for this user poi
     */
    @GetMapping("/users/{userId}/{poiId}")
    public ResponseEntity<PoiResponseDto> getPoiForUser(
            final @PathVariable java.util.UUID userId,
            final @PathVariable java.util.UUID poiId
    ) {
        log.info("Getting poi: [{}] for User [{}]", poiId, userId);
        return ResponseEntity.ok(poiService.getPoiForUser(userId, poiId));
    }

    /**
     * Returns all Pois fpr concrete {@link User}.
     *
     * @param userId id of concrete user
     * @return {@link PoiResponseDto} for all pois for this user
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity<List<PoiResponseDto>> getAllPoisForUser(final @PathVariable java.util.UUID userId) {
        log.info("Getting all pois, count: [{}] for user [{}]", poiService.getAllPois().size(), userId);
        return ResponseEntity.ok(poiService.getAllPoisForUser(userId));
    }

    /**
     * Updated poi.
     *
     * @param poiId poi id
     * @param updatedPoi updated poi
     * @return {@link PoiResponseDto} for updated poi
     */
    @PutMapping("/{poiId}")
    public ResponseEntity<PoiResponseDto> updatePoi(
            final @PathVariable java.util.UUID poiId,
            final @RequestBody @Valid PoiUpdateRequestDto updatedPoi
    ) {
        log.info("Updating poi: [{}]", poiId);
        return ResponseEntity.ok(poiService.updatePoi(poiId, updatedPoi));
    }

    /**
     * Deletes poi by id.
     *
     * @param poiId id of poi for delete
     */
    @DeleteMapping("/{poiId}")
    @PreAuthorize("@poiAccessGuard.canAccessPoiById(#poiId, authentication)")
    public ResponseEntity<Void> deletePoi(final @PathVariable java.util.UUID poiId) {
        if (poiId == null) {
            log.info("Poi not found: null");
        }

        log.info("Deleting poi: [{}]", poiId);
        poiService.deletePoi(poiId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Deletes poi for user by id.
     *
     * @param userId user id
     * @param poiId poi id
     * @return no content
     */
    @DeleteMapping("/users/{userId}/{poiId}")
    @PreAuthorize("@userAccessGuard.canAccessUserById(#userId, authentication)")
    public ResponseEntity<Void> deletePoiForUser(
            final @PathVariable java.util.UUID userId,
            final @PathVariable java.util.UUID poiId
    ) {
        log.info("Deleting poi: [{}] for user [{}]", poiId, userId);
        poiService.deletePoiForUser(userId, poiId);
        return ResponseEntity.noContent().build();
    }
}

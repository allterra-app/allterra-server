package com.allterra.server.model.gear;

import com.allterra.server.model.gear.dto.GearRequestDto;
import com.allterra.server.model.gear.dto.GearResponseDto;
import com.allterra.server.model.user.UserRepository;
import jakarta.validation.Valid;
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
 * Controller for gear inventory CRUD.
 */
@RestController
@RequestMapping("/gear")
@RequiredArgsConstructor
public final class GearItemController {

    private final GearItemService gearItemService;
    private final UserRepository userRepository;

    /**
     * Retrieves all gear items of authenticated user.
     * @param authentication authenticated principal
     * @return list of gear items
     */
    @GetMapping
    public List<GearResponseDto> getMyGear(final Authentication authentication) {
        UUID userId = userRepository.findByEmailIgnoreCase(requireUserEmail(authentication)).orElseThrow().getId();
        return gearItemService.getUserGear(userId);
    }

    /**
     * Retrieves single gear item by id.
     * @param id gear item id
     * @param authentication authenticated principal
     * @return gear item details
     */
    @GetMapping("/{id}")
    public GearResponseDto getGearItem(
            @PathVariable final UUID id,
            final Authentication authentication
    ) {
        UUID userId = userRepository.findByEmailIgnoreCase(requireUserEmail(authentication)).orElseThrow().getId();
        return gearItemService.getGearItem(id, userId);
    }

    /**
     * Creates a new gear item.
     * @param authentication authenticated principal
     * @param request gear payload
     * @return created gear item
     */
    @PostMapping
    public GearResponseDto createGearItem(
            final Authentication authentication,
            @Valid @RequestBody final GearRequestDto request
    ) {
        UUID userId = userRepository.findByEmailIgnoreCase(requireUserEmail(authentication)).orElseThrow().getId();
        return gearItemService.createGearItem(userId, request);
    }

    /**
     * Updates an existing gear item.
     * @param id gear item id
     * @param authentication authenticated principal
     * @param request update payload
     * @return updated gear item
     */
    @PutMapping("/{id}")
    public GearResponseDto updateGearItem(
            @PathVariable final UUID id,
            final Authentication authentication,
            @Valid @RequestBody final GearRequestDto request
    ) {
        UUID userId = userRepository.findByEmailIgnoreCase(requireUserEmail(authentication)).orElseThrow().getId();
        return gearItemService.updateGearItem(id, userId, request);
    }

    /**
     * Deletes gear item.
     * @param id gear item id
     * @param authentication authenticated principal
     * @return no content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGearItem(
            @PathVariable final UUID id,
            final Authentication authentication
    ) {
        UUID userId = userRepository.findByEmailIgnoreCase(requireUserEmail(authentication)).orElseThrow().getId();
        gearItemService.deleteGearItem(id, userId);
        return ResponseEntity.noContent().build();
    }

    private String requireUserEmail(final Authentication authentication) {
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            throw new IllegalArgumentException("Authenticated user is required");
        }
        return authentication.getName();
    }
}

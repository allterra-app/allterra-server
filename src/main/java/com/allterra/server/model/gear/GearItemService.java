package com.allterra.server.model.gear;

import com.allterra.server.model.gear.dto.GearRequestDto;
import com.allterra.server.model.gear.dto.GearResponseDto;
import com.allterra.server.model.user.User;
import com.allterra.server.model.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for managing gear inventory items.
 */
@Service
public class GearItemService {

    @Autowired
    private GearItemRepository gearItemRepository;
    @Autowired
    private UserRepository userRepository;

    /**
     * Retrieves all gear items for the specified user.
     *
     * @param userId the ID of the user
     * @return a list of gear items
     */
    @Transactional(readOnly = true)
    public List<GearResponseDto> getUserGear(final UUID userId) {
        return gearItemRepository.findAllByUserId(userId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a specific gear item by its ID for the specified user.
     *
     * @param id     the ID of the gear item
     * @param userId the ID of the user
     * @return the gear item details
     */
    @Transactional(readOnly = true)
    public GearResponseDto getGearItem(final UUID id, final UUID userId) {
        return gearItemRepository.findByIdAndUserId(id, userId)
                .map(this::mapToDto)
                .orElseThrow(() -> new RuntimeException("Gear item not found"));
    }

    /**
     * Creates a new gear item for the specified user.
     *
     * @param userId  the ID of the user
     * @param request the creation payload
     * @return the created gear item
     */
    @Transactional
    public GearResponseDto createGearItem(final UUID userId, final GearRequestDto request) {
        User user = userRepository.findById(userId).orElseThrow();
        GearItem gearItem = GearItem.builder()
                .name(request.getName())
                .category(request.getCategory())
                .weightKg(request.getWeightKg())
                .status(request.getStatus())
                .user(user)
                .build();

        return mapToDto(gearItemRepository.save(gearItem));
    }

    /**
     * Updates an existing gear item.
     *
     * @param id      the ID of the gear item
     * @param userId  the ID of the user
     * @param request the update payload
     * @return the updated gear item
     */
    @Transactional
    public GearResponseDto updateGearItem(final UUID id, final UUID userId, final GearRequestDto request) {
        GearItem gearItem = gearItemRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Gear item not found"));

        gearItem.setName(request.getName());
        gearItem.setCategory(request.getCategory());
        gearItem.setWeightKg(request.getWeightKg());
        gearItem.setStatus(request.getStatus());

        return mapToDto(gearItemRepository.save(gearItem));
    }

    /**
     * Deletes a gear item.
     *
     * @param id     the ID of the gear item
     * @param userId the ID of the user
     */
    @Transactional
    public void deleteGearItem(final UUID id, final UUID userId) {
        GearItem gearItem = gearItemRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Gear item not found"));
        gearItemRepository.delete(gearItem);
    }

    private GearResponseDto mapToDto(final GearItem item) {
        return GearResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .category(item.getCategory())
                .weightKg(item.getWeightKg())
                .status(item.getStatus())
                .userId(item.getUser().getId())
                .createdAt(item.getCreatedAt())
                .modifiedAt(item.getModifiedAt())
                .build();
    }
}

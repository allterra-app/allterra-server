package com.allterra.server.model.poi;

import com.allterra.server.exception.ResourceNotFoundException;
import com.allterra.server.model.poi.dto.PoiResponseDto;
import com.allterra.server.model.poi.dto.request.PoiCreateRequestDto;
import com.allterra.server.model.poi.dto.request.PoiUpdateRequestDto;
import com.allterra.server.model.user.User;
import com.allterra.server.model.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * Service fpr {@link Poi}.
 */
@Service
@RequiredArgsConstructor
public class PoiService {

    private final PoiRepository poiRepository;
    private final UserRepository userRepository;
    private final PoiMapper poiMapper;

    /**
     * Creates new Poi.
     *
     * @param requestDto request to create Poi
     * @return {@link PoiResponseDto} for created Poi
     */
    public PoiResponseDto createPoi(final PoiCreateRequestDto requestDto) {
        final var userId = requestDto.getUserId();
        if (userId == null) {
            throw new IllegalArgumentException("userId is required for poi creation");
        }

        final var poiEntity = poiMapper.toEntity(requestDto);
        final var savedPoi = poiRepository.save(poiEntity);
        final var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("User with id %s not found", userId)));
        if (user.getPois() == null) {
            user.setPois(new java.util.ArrayList<>());
        }
        user.getPois().add(savedPoi);
        userRepository.save(user);
        return poiMapper.toDto(savedPoi);
    }

    /**
     * Creates new Poi fot the User.
     *
     * @param userId user id
     * @param requestDto request to create Poi
     * @return {@link PoiResponseDto} for created Poi
     */
    public PoiResponseDto createPoiForUser(final java.util.UUID userId, PoiCreateRequestDto requestDto) {
        final var poiEntity = poiMapper.toEntity(requestDto);
        final var savedPoi = poiRepository.save(poiEntity);
        final var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("User with id %s not found", userId)));
        if (user.getPois() == null) {
            user.setPois(new java.util.ArrayList<>());
        }
        user.getPois().add(savedPoi);
        userRepository.save(user);
        return poiMapper.toDto(savedPoi);
    }

    /**
     * Returns Poi by id.
     *
     * @param poiId pois id
     * @return {@link PoiResponseDto} for poi
     */
    public PoiResponseDto getPoi(final java.util.UUID poiId) {
        return poiRepository.findById(poiId).map(poiMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Poi with id %s not found", poiId)));
    }

    /**
     * Returns all {@link Poi}.
     *
     * @return {@link PoiResponseDto} for all pois
     */
    public List<PoiResponseDto> getAllPois() {
        return poiRepository.findAll().stream()
                .map(poiMapper::toDto)
                .toList();
    }

    /**
     * Gets poi for user by id.
     *
     * @param userId user id
     * @param poiId poi id
     * @return {@link PoiResponseDto} for this user poi
     */
    public PoiResponseDto getPoiForUser(final java.util.UUID userId, final java.util.UUID poiId) {
        return userRepository.findById(userId)
                .flatMap(user -> user.getPois().stream()
                        .filter(poi -> poi.getId().equals(poiId))
                        .findFirst()
                )
                .map(poiMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Poi with id %s not found for user %s", poiId, userId)));
    }

    /**
     * Returns all Pois fpr concrete {@link User}.
     *
     * @param userId id of concrete user
     * @return {@link PoiResponseDto} for all pois for this user
     */
    public List<PoiResponseDto> getAllPoisForUser(final java.util.UUID userId) {
        return userRepository.findById(userId).map(user ->
                user.getPois().stream().map(poiMapper::toDto).toList()
        ).orElseGet(Collections::emptyList);
    }

    /**
     * Updated poi.
     *
     * @param poiId poi id
     * @param updatedPoi updated poi
     * @return {@link PoiResponseDto} for updated poi
     */
    public PoiResponseDto updatePoi(final java.util.UUID poiId, final PoiUpdateRequestDto updatedPoi) {
        return poiRepository.findById(poiId)
                .map(poi -> {
                    poiMapper.updateEntityFromDto(updatedPoi, poi);
                    return poiMapper.toDto(poiRepository.save(poi));
                })
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Poi with id %s not found", poiId)));
    }

    /**
     * Deletes poi by id.
     *
     * @param poiId id of poi for delete
     */
    public void deletePoi(final java.util.UUID poiId) {
        final var poi = poiRepository.findById(poiId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Poi with id %s not found", poiId)));
        poiRepository.delete(poi);
    }

    /**
     * Deletes poi for user by id.
     *
     * @param userId user id
     * @param poiId poi id
     */
    public void deletePoiForUser(final java.util.UUID userId, final java.util.UUID poiId) {
        final var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("User with id %s not found", userId)));
        final var poi = poiRepository.findById(poiId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Poi with id %s not found", poiId)));

        if (user.getPois() == null || user.getPois().stream().noneMatch(item -> item.getId().equals(poiId))) {
            throw new ResourceNotFoundException(
                    String.format("Poi with id %s not found for user %s", poiId, userId)
            );
        }

        user.getPois().removeIf(item -> item.getId().equals(poiId));
        userRepository.save(user);

        if (!poiRepository.existsByIdAndUsers_IdNot(poiId, userId)) {
            poiRepository.delete(poi);
        }
    }
}

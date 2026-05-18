package com.allterra.server.model.gear;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for gear items.
 */
public interface GearItemRepository extends JpaRepository<GearItem, UUID> {

    List<GearItem> findAllByUserId(UUID userId);

    Optional<GearItem> findByIdAndUserId(UUID id, UUID userId);
}

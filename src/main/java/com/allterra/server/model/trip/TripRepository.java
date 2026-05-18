package com.allterra.server.model.trip;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for Trip entity.
 */
@Repository
public interface TripRepository extends JpaRepository<Trip, UUID> {
    List<Trip> findAllByUserId(UUID userId);
}

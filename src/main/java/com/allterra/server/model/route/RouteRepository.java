package com.allterra.server.model.route;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Route repository.
 */
@Repository
public interface RouteRepository extends JpaRepository<Route, java.util.UUID> {
    List<Route> findAllByUser_IdOrderByCreatedAtDesc(java.util.UUID userId);
}

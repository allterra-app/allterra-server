package com.allterra.server.model.route;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Route repository.
 */
@Repository
public interface RouteRepository extends JpaRepository<Route, java.util.UUID> {
    @Override
    @EntityGraph(attributePaths = "user")
    Optional<Route> findById(java.util.UUID id);

    @Override
    @EntityGraph(attributePaths = "user")
    List<Route> findAll();

    @EntityGraph(attributePaths = "user")
    List<Route> findAllByUser_IdOrderByCreatedAtDesc(java.util.UUID userId);

    boolean existsByIdAndUser_EmailIgnoreCase(java.util.UUID id, String email);
}

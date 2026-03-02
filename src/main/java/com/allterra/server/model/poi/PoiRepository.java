package com.allterra.server.model.poi;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Implementation of {@link JpaRepository} for {@link Poi}.
 */
@Repository
public interface PoiRepository extends JpaRepository<Poi, java.util.UUID> {
    boolean existsByIdAndUsers_EmailIgnoreCase(java.util.UUID id, String email);

    boolean existsByIdAndUsers_IdNot(java.util.UUID id, java.util.UUID userId);
}

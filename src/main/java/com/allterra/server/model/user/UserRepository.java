package com.allterra.server.model.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Implementation {@link JpaRepository} for {@link User}.
 */
@Repository
public interface UserRepository extends JpaRepository<User, java.util.UUID> {
    Optional<User> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);
}

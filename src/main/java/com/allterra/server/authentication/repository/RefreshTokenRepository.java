package com.allterra.server.authentication.repository;

import com.allterra.server.authentication.model.RefreshToken;
import com.allterra.server.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for refresh tokens.
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, java.util.UUID> {
    Optional<RefreshToken> findByTokenHash(String tokenHash);

 java.util.UUID deleteByUser(User user);
}

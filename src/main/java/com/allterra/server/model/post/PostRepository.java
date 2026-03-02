package com.allterra.server.model.post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Implementation {@link JpaRepository} for {@link Post}.
 */
@Repository
public interface PostRepository extends JpaRepository<Post, java.util.UUID> {
    boolean existsByIdAndUser_EmailIgnoreCase(java.util.UUID id, String email);
}

package com.allterra.server.model.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Implementation {@link JpaRepository} for {@link Post}.
 */
@Repository
public interface PostRepository extends JpaRepository<Post, java.util.UUID> {
    List<Post> findAllByOrderByCreatedAtDesc();

    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<Post> findAllByAudienceOrderByCreatedAtDesc(PostAudience audience, Pageable pageable);

    List<Post> findAllByUserIdOrderByCreatedAtDesc(java.util.UUID userId);

    boolean existsByIdAndUser_EmailIgnoreCase(java.util.UUID id, String email);
}

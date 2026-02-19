package com.allterra.server.photo.repository;

import com.allterra.server.photo.model.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Base {@link JpaRepository} implementation for {@link Photo}.
 *
 * @param <T> entity type
 */
@NoRepositoryBean
public interface PhotoRepository<T extends Photo> extends JpaRepository<T, java.util.UUID> {
}

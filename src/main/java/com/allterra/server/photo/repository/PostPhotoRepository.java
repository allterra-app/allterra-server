package com.allterra.server.photo.repository;

import com.allterra.server.photo.model.PostPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * {@link JpaRepository} implementation for {@link PostPhoto}.
 */
@Repository
public interface PostPhotoRepository extends PhotoRepository<PostPhoto> {
}

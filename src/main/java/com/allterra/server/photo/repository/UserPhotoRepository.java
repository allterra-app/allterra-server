package com.allterra.server.photo.repository;

import com.allterra.server.photo.model.UserPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * {@link JpaRepository} implementation for {@link UserPhoto}.
 */
@Repository
public interface UserPhotoRepository extends PhotoRepository<UserPhoto> {
}

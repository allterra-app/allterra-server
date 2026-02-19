package com.allterra.server.photo.repository;

import com.allterra.server.photo.model.PoiPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * {@link JpaRepository} implementation for {@link PoiPhoto}.
 */
@Repository
public interface PoiPhotoRepository extends PhotoRepository<PoiPhoto> {
}

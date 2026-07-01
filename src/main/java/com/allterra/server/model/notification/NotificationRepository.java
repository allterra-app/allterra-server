package com.allterra.server.model.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for {@link Notification}.
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    /**
     * Returns notifications for user ordered by creation date descending.
     *
     * @param userId user id
     * @return notifications for user
     */
    List<Notification> findAllByUserIdOrderByCreatedAtDesc(UUID userId);
}

package com.allterra.server.model.notification;

import com.allterra.server.exception.ResourceNotFoundException;
import com.allterra.server.model.notification.dto.NotificationResponseDto;
import com.allterra.server.model.user.User;
import com.allterra.server.model.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Service for {@link Notification}.
 */
@Service
@RequiredArgsConstructor
@SuppressWarnings("checkstyle:DesignForExtension")
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    /**
     * Returns notifications for user email.
     *
     * @param email user email
     * @return notifications for user
     */
    public List<NotificationResponseDto> getForUserEmail(final String email) {
        if (email == null || email.isBlank()) {
            return java.util.Collections.emptyList();
        }
        final var user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("User with email %s not found", email)));
        return notificationRepository.findAllByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(this::toDto)
                .toList();
    }

    /**
     * Creates notification for user.
     *
     * @param user user
     * @param title notification title
     * @param body notification body
     */
    public void createForUser(final User user, final String title, final String body) {
        notificationRepository.save(Notification.builder()
                .user(user)
                .title(title)
                .body(body)
                .read(false)
                .build());
    }

    /**
     * Marks notification as read.
     *
     * @param id notification id
     * @param email user email
     * @return updated notification
     */
    public NotificationResponseDto markRead(final UUID id, final String email) {
        if (email == null || email.isBlank()) {
            throw new ResourceNotFoundException(String.format("Notification with id %s not found for user", id));
        }
        final var notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Notification with id %s not found", id)));
        if (notification.getUser() == null || !email.equalsIgnoreCase(notification.getUser().getEmail())) {
            throw new ResourceNotFoundException(String.format("Notification with id %s not found for user", id));
        }
        notification.setRead(true);
        return toDto(notificationRepository.save(notification));
    }

    private NotificationResponseDto toDto(final Notification notification) {
        return NotificationResponseDto.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .body(notification.getBody())
                .read(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}

package com.allterra.server.model.notification;

import com.allterra.server.model.notification.dto.NotificationResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * Controller for {@link Notification}.
 */
@Slf4j
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@SuppressWarnings("checkstyle:DesignForExtension")
public class NotificationController {
    private final NotificationService notificationService;

    /**
     * Returns notifications for authenticated user.
     *
     * @param authentication authentication object
     * @return notifications for authenticated user
     */
    @GetMapping("/me")
    public ResponseEntity<List<NotificationResponseDto>> getMyNotifications(final Authentication authentication) {
        final var email = authentication == null ? null : authentication.getName();
        log.info("Get notifications for email {}", email);
        return ResponseEntity.ok(notificationService.getForUserEmail(email));
    }

    /**
     * Marks notification as read.
     *
     * @param id notification id
     * @param authentication authentication object
     * @return updated notification
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<NotificationResponseDto> markRead(
            @PathVariable final UUID id,
            final Authentication authentication
    ) {
        final var email = authentication == null ? null : authentication.getName();
        log.info("Mark notification {} as read for email {}", id, email);
        return ResponseEntity.ok(notificationService.markRead(id, email));
    }
}

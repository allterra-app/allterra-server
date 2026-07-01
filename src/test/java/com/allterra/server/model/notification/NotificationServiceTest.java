package com.allterra.server.model.notification;

import com.allterra.server.exception.ResourceNotFoundException;
import com.allterra.server.model.user.User;
import com.allterra.server.model.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    void getForUserEmailShouldReturnEmptyForNullEmail() {
        assertThat(notificationService.getForUserEmail(null)).isEmpty();
    }

    @Test
    void getForUserEmailShouldReturnEmptyForBlankEmail() {
        assertThat(notificationService.getForUserEmail("  ")).isEmpty();
    }

    @Test
    void markReadShouldThrowWhenEmailIsNull() {
        assertThatThrownBy(() -> notificationService.markRead(
                com.allterra.server.TestUuids.id(1), null))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void markReadShouldThrowWhenEmailIsBlank() {
        assertThatThrownBy(() -> notificationService.markRead(
                com.allterra.server.TestUuids.id(1), "  "))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void markReadShouldThrowWhenNotificationNotFound() {
        when(notificationRepository.findById(com.allterra.server.TestUuids.id(1)))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificationService.markRead(
                com.allterra.server.TestUuids.id(1), "user@example.com"))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}

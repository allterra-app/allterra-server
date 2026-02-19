package com.allterra.server.model.user;

import com.allterra.server.model.user.dto.UserResponseDto;
import com.allterra.server.model.user.dto.request.UserCreateRequestDto;
import com.allterra.server.model.user.dto.request.UserRolesUpdateRequestDto;
import com.allterra.server.model.user.dto.request.UserSubscriptionPurchaseRequestDto;
import com.allterra.server.model.user.dto.request.UserUpdateRequestDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void getAllShouldReturnUsersFromService() {
        var response = UserResponseDto.builder().id(com.allterra.server.TestUuids.id(1)).email("john@allterra.com").build();
        when(userService.getAll()).thenReturn(List.of(response));

        assertThat(userController.getAll()).containsExactly(response);
    }

    @Test
    void getByIdShouldReturnOkWhenUserExists() {
        var response = UserResponseDto.builder().id(com.allterra.server.TestUuids.id(1)).build();
        when(userService.getById(com.allterra.server.TestUuids.id(1))).thenReturn(response);

        var result = userController.getById(com.allterra.server.TestUuids.id(1));

        assertThat(result.getStatusCode().value()).isEqualTo(200);
        assertThat(result.getBody()).isEqualTo(response);
    }

    @Test
    void getByIdShouldReturnNotFoundWhenMissing() {
        when(userService.getById(com.allterra.server.TestUuids.id(1))).thenReturn(null);

        var result = userController.getById(com.allterra.server.TestUuids.id(1));

        assertThat(result.getStatusCode().value()).isEqualTo(404);
    }

    @Test
    void createShouldReturnCreatedUser() {
        var request = UserCreateRequestDto.builder().email("john@allterra.com").build();
        var response = UserResponseDto.builder().id(com.allterra.server.TestUuids.id(1)).email("john@allterra.com").build();
        when(userService.create(request)).thenReturn(response);

        var result = userController.create(request);

        assertThat(result.getStatusCode().value()).isEqualTo(200);
        assertThat(result.getBody()).isEqualTo(response);
    }

    @Test
    void updateShouldReturnNotFoundWhenMissing() {
        var request = UserUpdateRequestDto.builder().id(com.allterra.server.TestUuids.id(1)).build();
        when(userService.update(com.allterra.server.TestUuids.id(1), request)).thenReturn(null);

        var result = userController.update(com.allterra.server.TestUuids.id(1), request);

        assertThat(result.getStatusCode().value()).isEqualTo(404);
    }

    @Test
    void deleteShouldReturnNoContent() {
        var result = userController.delete(com.allterra.server.TestUuids.id(5));

        assertThat(result.getStatusCode().value()).isEqualTo(204);
        verify(userService).delete(com.allterra.server.TestUuids.id(5));
    }

    @Test
    void purchaseSubscriptionShouldReturnOk() {
        var request = UserSubscriptionPurchaseRequestDto.builder().plan(SubscriptionPlan.MONTHLY).build();
        var response = UserResponseDto.builder().id(com.allterra.server.TestUuids.id(1)).subscriptionPlan(SubscriptionPlan.MONTHLY).build();
        when(userService.purchaseSubscription(com.allterra.server.TestUuids.id(1), request)).thenReturn(response);

        var result = userController.purchaseSubscription(com.allterra.server.TestUuids.id(1), request);

        assertThat(result.getStatusCode().value()).isEqualTo(200);
        assertThat(result.getBody()).isEqualTo(response);
    }

    @Test
    void updateRolesShouldReturnOk() {
        var request = UserRolesUpdateRequestDto.builder().roles(Set.of(UserRole.ADMIN)).build();
        var response = UserResponseDto.builder().id(com.allterra.server.TestUuids.id(1)).roles(Set.of(UserRole.ADMIN)).build();
        when(userService.updateRoles(com.allterra.server.TestUuids.id(1), request)).thenReturn(response);

        var result = userController.updateRoles(com.allterra.server.TestUuids.id(1), request);

        assertThat(result.getStatusCode().value()).isEqualTo(200);
        assertThat(result.getBody()).isEqualTo(response);
    }

    @Test
    void exportUserDataShouldReturnOk() {
        var response = UserResponseDto.builder().id(com.allterra.server.TestUuids.id(1)).email("john@allterra.com").build();
        when(userService.exportUserData(com.allterra.server.TestUuids.id(1))).thenReturn(response);

        var result = userController.exportUserData(com.allterra.server.TestUuids.id(1));

        assertThat(result.getStatusCode().value()).isEqualTo(200);
        assertThat(result.getBody()).isEqualTo(response);
    }

    @Test
    void verifyEmailShouldReturnOk() {
        var response = UserResponseDto.builder().id(com.allterra.server.TestUuids.id(1)).emailVerified(true).build();
        when(userService.verifyEmail(com.allterra.server.TestUuids.id(1))).thenReturn(response);

        var result = userController.verifyEmail(com.allterra.server.TestUuids.id(1));

        assertThat(result.getStatusCode().value()).isEqualTo(200);
        assertThat(result.getBody()).isEqualTo(response);
    }
}

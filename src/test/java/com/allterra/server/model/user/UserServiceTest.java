package com.allterra.server.model.user;

import com.allterra.server.authentication.dto.JwtAuthenticationRequestDto;
import com.allterra.server.exception.EmailAlreadyTakenException;
import com.allterra.server.model.user.dto.request.UserRolesUpdateRequestDto;
import com.allterra.server.model.user.dto.request.UserSubscriptionPurchaseRequestDto;
import com.allterra.server.model.user.dto.UserResponseDto;
import com.allterra.server.model.user.dto.request.UserCreateRequestDto;
import com.allterra.server.model.user.dto.request.UserUpdateRequestDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserMapper userMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void getAllShouldReturnMappedUsers() {
        var user = User.builder().id(com.allterra.server.TestUuids.id(1)).email("john@allterra.com").build();
        var dto = UserResponseDto.builder().id(com.allterra.server.TestUuids.id(1)).email("john@allterra.com").build();

        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toDto(user)).thenReturn(dto);

        var result = userService.getAll();

        assertThat(result).containsExactly(dto);
    }

    @Test
    void getByIdShouldReturnNullWhenMissing() {
        when(userRepository.findById(com.allterra.server.TestUuids.id(99))).thenReturn(Optional.empty());

        assertThat(userService.getById(com.allterra.server.TestUuids.id(99))).isNull();
    }

    @Test
    void createShouldSaveMappedEntity() {
        var request = UserCreateRequestDto.builder().email("john@allterra.com").build();
        var entity = User.builder().email("john@allterra.com").build();
        var saved = User.builder().id(com.allterra.server.TestUuids.id(1)).email("john@allterra.com").build();
        var response = UserResponseDto.builder().id(com.allterra.server.TestUuids.id(1)).email("john@allterra.com").build();

        when(userMapper.toEntity(request)).thenReturn(entity);
        when(userRepository.save(entity)).thenReturn(saved);
        when(userMapper.toDto(saved)).thenReturn(response);

        assertThat(userService.create(request)).isEqualTo(response);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateShouldReturnNullWhenUserMissing() {
        when(userRepository.findById(com.allterra.server.TestUuids.id(77))).thenReturn(Optional.empty());

        assertThat(userService.update(com.allterra.server.TestUuids.id(77), new UserUpdateRequestDto())).isNull();
    }

    @Test
    void updateShouldAttachUserPhotoWhenProvided() {
        var user = User.builder().id(com.allterra.server.TestUuids.id(1)).build();
        var updateRequest = UserUpdateRequestDto.builder().id(com.allterra.server.TestUuids.id(1)).userPhoto("https://cdn/photo.jpg").build();
        var response = UserResponseDto.builder().id(com.allterra.server.TestUuids.id(1)).build();

        when(userRepository.findById(com.allterra.server.TestUuids.id(1))).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userMapper.toDto(any(User.class))).thenReturn(response);

        var result = userService.update(com.allterra.server.TestUuids.id(1), updateRequest);

        assertThat(result).isEqualTo(response);
        assertThat(user.getUserPhoto()).isNotNull();
        assertThat(user.getUserPhoto().getUrl()).isEqualTo("https://cdn/photo.jpg");
        assertThat(user.getUserPhoto().getUser()).isEqualTo(user);
    }

    @Test
    void deleteShouldSoftDeleteUser() {
        var user = User.builder().id(com.allterra.server.TestUuids.id(5)).build();
        when(userRepository.findById(com.allterra.server.TestUuids.id(5))).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.delete(com.allterra.server.TestUuids.id(5));

        assertThat(user.getDeletedAt()).isNotNull();
        verify(userRepository).save(user);
    }

    @Test
    void registerUserShouldThrowWhenEmailAlreadyExists() {
        var request = new JwtAuthenticationRequestDto("john@allterra.com", "secret");
        when(userRepository.existsByEmailIgnoreCase("john@allterra.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.registerUser(request))
                .isInstanceOf(EmailAlreadyTakenException.class)
                .hasMessageContaining("john@allterra.com");
    }

    @Test
    void registerUserShouldEncodePasswordAndSaveUser() {
        var request = new JwtAuthenticationRequestDto("john@allterra.com", "secret");
        var saved = User.builder().id(com.allterra.server.TestUuids.id(10)).email("john@allterra.com").password("encoded").build();
        var response = UserResponseDto.builder().id(com.allterra.server.TestUuids.id(10)).email("john@allterra.com").build();

        when(userRepository.existsByEmailIgnoreCase("john@allterra.com")).thenReturn(false);
        when(passwordEncoder.encode("secret")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenReturn(saved);
        when(userMapper.toDto(saved)).thenReturn(response);

        var result = userService.registerUser(request);

        assertThat(result).isEqualTo(response);
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getEmail()).isEqualTo("john@allterra.com");
        assertThat(userCaptor.getValue().getPassword()).isEqualTo("encoded");
        assertThat(userCaptor.getValue().getRoles()).containsExactly(UserRole.USER);
        assertThat(userCaptor.getValue().getSubscriptionPlan()).isEqualTo(SubscriptionPlan.FREE);
    }

    @Test
    void updateRolesShouldReturnUpdatedUser() {
        var user = User.builder().id(com.allterra.server.TestUuids.id(11)).roles(Set.of(UserRole.USER)).subscriptionPlan(SubscriptionPlan.FREE).build();
        var request = UserRolesUpdateRequestDto.builder().roles(Set.of(UserRole.ADMIN)).build();
        var response = UserResponseDto.builder().id(com.allterra.server.TestUuids.id(11)).roles(Set.of(UserRole.ADMIN)).build();

        when(userRepository.findById(com.allterra.server.TestUuids.id(11))).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userMapper.toDto(any(User.class))).thenReturn(response);

        var result = userService.updateRoles(com.allterra.server.TestUuids.id(11), request);

        assertThat(result).isEqualTo(response);
        assertThat(user.getRoles()).containsExactly(UserRole.ADMIN);
    }

    @Test
    void purchaseSubscriptionShouldSetPlanAndExpiration() {
        var user = User.builder().id(com.allterra.server.TestUuids.id(12)).roles(Set.of(UserRole.USER)).subscriptionPlan(SubscriptionPlan.FREE).build();
        var request = UserSubscriptionPurchaseRequestDto.builder().plan(SubscriptionPlan.MONTHLY).build();
        var response = UserResponseDto.builder().id(com.allterra.server.TestUuids.id(12)).subscriptionPlan(SubscriptionPlan.MONTHLY).build();

        when(userRepository.findById(com.allterra.server.TestUuids.id(12))).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userMapper.toDto(any(User.class))).thenReturn(response);

        var result = userService.purchaseSubscription(com.allterra.server.TestUuids.id(12), request);

        assertThat(result).isEqualTo(response);
        assertThat(user.getSubscriptionPlan()).isEqualTo(SubscriptionPlan.MONTHLY);
        assertThat(user.getSubscriptionStartedAt()).isNotNull();
        assertThat(user.getSubscriptionExpiresAt()).isNotNull();
        assertThat(user.getSubscriptionExpiresAt()).isAfter(user.getSubscriptionStartedAt());
    }

    @Test
    void exportUserDataShouldReturnUserWhenExists() {
        var user = User.builder().id(com.allterra.server.TestUuids.id(99)).build();
        var response = UserResponseDto.builder().id(com.allterra.server.TestUuids.id(99)).build();
        when(userRepository.findById(com.allterra.server.TestUuids.id(99))).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(response);

        var result = userService.exportUserData(com.allterra.server.TestUuids.id(99));

        assertThat(result).isEqualTo(response);
    }

    @Test
    void verifyEmailShouldMarkUserAsVerified() {
        var user = User.builder().id(com.allterra.server.TestUuids.id(50)).emailVerified(false).build();
        var response = UserResponseDto.builder().id(com.allterra.server.TestUuids.id(50)).emailVerified(true).build();
        when(userRepository.findById(com.allterra.server.TestUuids.id(50))).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(response);

        var result = userService.verifyEmail(com.allterra.server.TestUuids.id(50));

        assertThat(result).isEqualTo(response);
        assertThat(user.isEmailVerified()).isTrue();
    }
}

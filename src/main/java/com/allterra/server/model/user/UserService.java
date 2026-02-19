package com.allterra.server.model.user;

import com.allterra.server.authentication.dto.JwtAuthenticationRequestDto;
import com.allterra.server.exception.EmailAlreadyTakenException;
import com.allterra.server.model.user.dto.UserResponseDto;
import com.allterra.server.model.user.dto.request.UserCreateRequestDto;
import com.allterra.server.model.user.dto.request.UserRolesUpdateRequestDto;
import com.allterra.server.model.user.dto.request.UserSubscriptionPurchaseRequestDto;
import com.allterra.server.model.user.dto.request.UserUpdateRequestDto;
import com.allterra.server.photo.model.UserPhoto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.List;

/**
 * User service.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public final class UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Gets all users.
     *
     * @return list of {@link UserResponseDto}
     */
    public List<UserResponseDto> getAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .toList();
    }

    /**
     * Returns user by id.
     *
     * @param id user id
     * @return {@link UserResponseDto} by user id
     */
    public UserResponseDto getById(final java.util.UUID id) {
        return userRepository.findById(id)
                .map(userMapper::toDto)
                .orElse(null);
    }

    /**
     * Created user.
     *
     * @param userRequestDto {@link UserCreateRequestDto} for user
     * @return {@link UserResponseDto} for created user
     */
    public UserResponseDto create(final UserCreateRequestDto userRequestDto) {
        var user = userMapper.toEntity(userRequestDto);
        ensureDefaults(user);

        return userMapper.toDto(userRepository.save(user));
    }

    /**
     * Update user by id.
     *
     * @param id user id
     * @param updatedUserDto user to be updated
     */
    public UserResponseDto update(final java.util.UUID id, final UserUpdateRequestDto updatedUserDto) {
        return userRepository.findById(id)
                .map(user -> {
                    final var userPhoto = updatedUserDto.getUserPhoto();

                    if (userPhoto != null) {
                        user.setUserPhoto(
                                UserPhoto.builder()
                                        .url(userPhoto)
                                        .user(user)
                                        .build()
                        );
                    }

                    userMapper.updateEntityFromDto(updatedUserDto, user);
                    ensureDefaults(user);
                    return userMapper.toDto(userRepository.save(user));
                })
                .orElse(null);
    }

    /**
     * Soft deletes user by id.
     *
     * @param id user id
     */
    public void delete(final java.util.UUID id) {
        userRepository.findById(id).ifPresent(user -> {
            user.setDeletedAt(java.time.LocalDateTime.now());
            userRepository.save(user);
        });
    }

    /**
     * Registers a new user.
     *
     * @param authenticationRequest {@link JwtAuthenticationRequestDto} containing user registration details
     * @return {@link UserResponseDto} for the registered user
     * @throws RuntimeException if the email or username is already taken
     */
    public UserResponseDto registerUser(final JwtAuthenticationRequestDto authenticationRequest) {
        var email = authenticationRequest.email();
        if (existsByEmail(email)) {
            log.error("User with the provided email already exists");
            throw new EmailAlreadyTakenException(email);
        }

        var user = User.builder()
                .email(email)
                .emailVerified(false)
                .password(passwordEncoder.encode(authenticationRequest.password()))
                .roles(EnumSet.of(UserRole.USER))
                .subscriptionPlan(SubscriptionPlan.FREE)
                .build();

        log.debug("User {} successfully registered", user.getId());
        return userMapper.toDto(userRepository.save(user));
    }

    /**
     * Updates user roles.
     *
     * @param id user id
     * @param request roles request
     * @return updated user or {@code null} if user is missing
     */
    public UserResponseDto updateRoles(final java.util.UUID id, final UserRolesUpdateRequestDto request) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setRoles(request.getRoles());
                    ensureDefaults(user);
                    return userMapper.toDto(userRepository.save(user));
                })
                .orElse(null);
    }

    /**
     * Purchases subscription for user.
     *
     * @param id user id
     * @param request subscription purchase request
     * @return updated user or {@code null} if user is missing
     */
    public UserResponseDto purchaseSubscription(final java.util.UUID id, final UserSubscriptionPurchaseRequestDto request) {
        return userRepository.findById(id)
                .map(user -> {
                    var now = java.time.LocalDateTime.now();
                    user.setSubscriptionPlan(request.getPlan());
                    user.setSubscriptionStartedAt(now);

                    int durationDays = request.getDurationDays() != null
                            ? request.getDurationDays()
                            : request.getPlan().getDurationDays();

                    if (durationDays > 0) {
                        user.setSubscriptionExpiresAt(now.plusDays(durationDays));
                    } else {
                        user.setSubscriptionExpiresAt(null);
                    }

                    ensureDefaults(user);
                    return userMapper.toDto(userRepository.save(user));
                })
                .orElse(null);
    }

    /**
     * Exports user data.
     *
     * @param id user id
     * @return user data or {@code null} if user is missing
     */
    public UserResponseDto exportUserData(final java.util.UUID id) {
        return getById(id);
    }

    /**
     * Marks user email as verified.
     *
     * @param id user id
     * @return updated user or {@code null} if user is missing
     */
    public UserResponseDto verifyEmail(final java.util.UUID id) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setEmailVerified(true);
                    return userMapper.toDto(userRepository.save(user));
                })
                .orElse(null);
    }

    private boolean existsByEmail(final String email) {
        return userRepository.existsByEmailIgnoreCase(email);
    }

    private void ensureDefaults(final User user) {
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            user.setRoles(EnumSet.of(UserRole.USER));
        }
        if (user.getSubscriptionPlan() == null) {
            user.setSubscriptionPlan(SubscriptionPlan.FREE);
        }
    }
}

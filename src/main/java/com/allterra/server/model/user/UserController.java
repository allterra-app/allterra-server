package com.allterra.server.model.user;

import com.allterra.server.model.user.dto.UserResponseDto;
import com.allterra.server.model.user.dto.request.UserCreateRequestDto;
import com.allterra.server.model.user.dto.request.UserRolesUpdateRequestDto;
import com.allterra.server.model.user.dto.request.UserSubscriptionPurchaseRequestDto;
import com.allterra.server.model.user.dto.request.UserUpdateRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static java.util.Objects.nonNull;

/**
 * Controller for {@link User}.
 */
@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    /**
     * Returns all active users.
     *
     * <p>This method contains no extension-specific invariants and may be safely overridden.</p>
     *
     * @return list of users
     */
    @GetMapping
    public List<UserResponseDto> getAll() {
        log.info("Get all users requested.");
        return userService.getAll();
    }

    /**
     * Returns user by id.
     *
     * @param id user id
     * @return {@link ResponseEntity} for user by id
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userAccessGuard.canAccessUserById(#id, authentication)")
    public ResponseEntity<UserResponseDto> getById(final @PathVariable java.util.UUID id) {
        log.info("Get user by id {} requested.", id);
        var userResponseDto = userService.getById(id);

        return nonNull(userResponseDto) ? ResponseEntity.ok(userResponseDto) : ResponseEntity.notFound().build();
    }

    /**
     * Returns current authenticated user.
     *
     * @param authentication authentication context
     * @return current user
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getCurrentUser(final Authentication authentication) {
        var email = authentication == null ? null : authentication.getName();
        log.info("Get current user requested for email {}.", email);
        var userResponseDto = userService.getByEmail(email);
        return nonNull(userResponseDto) ? ResponseEntity.ok(userResponseDto) : ResponseEntity.notFound().build();
    }

    /**
     * Creates user.
     *
     * @param userDto user dto model
     * @return {@link ResponseEntity} for created user
     */
    @PostMapping
    public ResponseEntity<UserResponseDto> create(final @RequestBody @Valid UserCreateRequestDto userDto) {
        log.info("Create user requested.");
        var createdUser = userService.create(userDto);

        return ResponseEntity.ok(createdUser);
    }

    /**
     * Update user by id.
     *
     * @param id user id
     * @param userDto user dto model
     * @return {@link ResponseEntity} for updated user
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userAccessGuard.canAccessUserById(#id, authentication)")
    public ResponseEntity<UserResponseDto> update(
            final @PathVariable java.util.UUID id,
            final @RequestBody @Valid UserUpdateRequestDto userDto
    ) {
        log.info("Update user by id {} requested.", id);
        var updatedUser = userService.update(id, userDto);

        return nonNull(updatedUser) ? ResponseEntity.ok(updatedUser) : ResponseEntity.notFound().build();
    }

    /**
     * Delete user by id.
     *
     * @param id user id
     * @return {@link ResponseEntity} for updated user
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userAccessGuard.canAccessUserById(#id, authentication)")
    public ResponseEntity<Void> delete(final @PathVariable java.util.UUID id) {
        log.info("Delete user by id {} requested.", id);
        userService.delete(id);

        return ResponseEntity.noContent().build();
    }

    /**
     * Purchases subscription for user by id.
     *
     * @param id user id
     * @param request subscription purchase request
     * @return updated user
     */
    @PostMapping("/{id}/subscription/purchase")
    @PreAuthorize("hasRole('ADMIN') or @userAccessGuard.canAccessUserById(#id, authentication)")
    public ResponseEntity<UserResponseDto> purchaseSubscription(
            final @PathVariable java.util.UUID id,
            final @RequestBody @Valid UserSubscriptionPurchaseRequestDto request
    ) {
        log.info("Purchase subscription for user id {} requested.", id);
        var updatedUser = userService.purchaseSubscription(id, request);
        return nonNull(updatedUser) ? ResponseEntity.ok(updatedUser) : ResponseEntity.notFound().build();
    }

    /**
     * Updates user roles by id.
     *
     * @param id user id
     * @param request roles update request
     * @return updated user
     */
    @PutMapping("/{id}/roles")
    public ResponseEntity<UserResponseDto> updateRoles(
            final @PathVariable java.util.UUID id,
            final @RequestBody @Valid UserRolesUpdateRequestDto request
    ) {
        log.info("Update roles for user id {} requested.", id);
        var updatedUser = userService.updateRoles(id, request);
        return nonNull(updatedUser) ? ResponseEntity.ok(updatedUser) : ResponseEntity.notFound().build();
    }

    /**
     * Exports user data by id.
     *
     * @param id user id
     * @return user data for export
     */
    @GetMapping("/{id}/export")
    @PreAuthorize("hasRole('ADMIN') or @userAccessGuard.canAccessUserById(#id, authentication)")
    public ResponseEntity<UserResponseDto> exportUserData(final @PathVariable java.util.UUID id) {
        log.info("Export user data requested for id {}.", id);
        var userData = userService.exportUserData(id);
        return nonNull(userData) ? ResponseEntity.ok(userData) : ResponseEntity.notFound().build();
    }

    /**
     * Marks user email as verified.
     *
     * @param id user id
     * @return updated user
     */
    @PutMapping("/{id}/verify-email")
    @PreAuthorize("hasRole('ADMIN') or @userAccessGuard.canAccessUserById(#id, authentication)")
    public ResponseEntity<UserResponseDto> verifyEmail(final @PathVariable java.util.UUID id) {
        log.info("Verify email requested for user id {}.", id);
        var updatedUser = userService.verifyEmail(id);
        return nonNull(updatedUser) ? ResponseEntity.ok(updatedUser) : ResponseEntity.notFound().build();
    }
}

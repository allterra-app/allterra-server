package com.allterra.server.model.user;

import com.allterra.server.model.user.dto.UserResponseDto;
import com.allterra.server.model.user.dto.request.UserCreateRequestDto;
import com.allterra.server.model.user.dto.request.UserUpdateRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Mapper for {@link User}.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {
    DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @Named("stringToLocalDate")
    default LocalDate stringToLocalDate(final String date) {
        return date != null ? LocalDate.parse(date, FORMATTER) : null;
    }

    @Named("localDateToString")
    default String localDateToString(LocalDate date) {
        return date != null ? date.format(FORMATTER) : null;
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "subscriptionPlan", ignore = true)
    @Mapping(target = "subscriptionStartedAt", ignore = true)
    @Mapping(target = "subscriptionExpiresAt", ignore = true)
    @Mapping(target = "emailVerified", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "savedPosts", ignore = true)
    @Mapping(target = "birthDate", source = "birthDate", qualifiedByName = "stringToLocalDate")
    User toEntity(final UserCreateRequestDto userCreateRequestDto);

    @Mapping(target = "birthDate", source = "birthDate", qualifiedByName = "localDateToString")
    UserResponseDto toDto(final User user);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "userPhoto", ignore = true)
    @Mapping(target = "pois", ignore = true)
    @Mapping(target = "posts", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "subscriptionPlan", ignore = true)
    @Mapping(target = "subscriptionStartedAt", ignore = true)
    @Mapping(target = "subscriptionExpiresAt", ignore = true)
    @Mapping(target = "emailVerified", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "savedPosts", ignore = true)
    @Mapping(target = "birthDate", source = "birthDate", qualifiedByName = "stringToLocalDate")
    void updateEntityFromDto(final UserUpdateRequestDto userUpdateRequestDto, final @MappingTarget User user);
}

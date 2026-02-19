package com.allterra.server.model.poi.dto.request;

import com.allterra.server.model.poi.Poi;
import com.allterra.server.model.user.User;
import com.allterra.server.photo.model.PoiPhoto;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request DTO model for {@link Poi} updating.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PoiUpdateRequestDto {

    @NotNull(message = "ID is required")
    private java.util.UUID id;

    @NotNull(message = "Name is required")
    private String name;

    private String description;

    private List<User> users;

    private int rating;

    private boolean actual;

    private String url;

    private PoiPhoto poiPhoto;
}

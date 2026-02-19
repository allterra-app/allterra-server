package com.allterra.server.model.poi.dto;

import com.allterra.server.model.user.User;
import com.allterra.server.photo.model.PoiPhoto;
import com.allterra.server.model.poi.Poi;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO model for {@link Poi} creating.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PoiResponseDto {

    private java.util.UUID id;

    private String name;

    private String description;

    private List<User> users;

    private int rating;

    private boolean actual;

    private String url;

    private PoiPhoto poiPhoto;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;
}

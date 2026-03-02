package com.allterra.server.model.poi.dto;

import com.allterra.server.model.poi.Poi;
import com.allterra.server.model.poi.PoiType;
import com.allterra.server.photo.dto.PoiPhotoResponseDto;
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

    private List<java.util.UUID> userIds;

    private int rating;

    private boolean actual;

    private String url;

    private PoiType type;

    private List<PoiPhotoResponseDto> photos;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;
}

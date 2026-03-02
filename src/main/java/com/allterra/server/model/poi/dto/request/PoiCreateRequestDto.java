package com.allterra.server.model.poi.dto.request;

import com.allterra.server.model.poi.Poi;
import com.allterra.server.model.poi.PoiType;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO model for {@link Poi} creating.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PoiCreateRequestDto {

    private java.util.UUID userId;

    @NotBlank
    private String name;

    private String description;

    private int rating;

    private boolean actual;

    private String url;

    private PoiType type;
}

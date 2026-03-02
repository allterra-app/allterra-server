package com.allterra.server.model.route;

import com.allterra.server.model.route.dto.request.RouteCreateRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RouteCreateRequestDtoJsonTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldDeserializePayloadWithJsonCharsetCompatibleShape() throws Exception {
        var json = """
                {
                  "userId": "00000000-0000-0000-0000-000000000001",
                  "title": "Route",
                  "description": "Desc",
                  "gpxContent": "<gpx><trk><trkseg><trkpt lat=\\"1\\" lon=\\"1\\"/></trkseg></trk></gpx>",
                  "distanceKm": 3.1,
                  "durationMinutes": 10,
                  "pointCount": 2
                }
                """;

        var dto = objectMapper.readValue(json, RouteCreateRequestDto.class);

        assertThat(dto.getTitle()).isEqualTo("Route");
        assertThat(dto.getGpxContent()).contains("<gpx>");
        assertThat(dto.getUserId()).isNotNull();
    }
}

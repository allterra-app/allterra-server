package com.allterra.server.model.route.gpx;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GpxRouteParserServiceTest {

    private final GpxRouteParserService parserService = new GpxRouteParserService();

    @Test
    void parseShouldReturnMetricsForTrackPoints() {
        var gpx = """
                <gpx version="1.1" creator="allterra-tests" xmlns="http://www.topografix.com/GPX/1/1">
                  <trk>
                    <name>Morning route</name>
                    <trkseg>
                      <trkpt lat="51.8402" lon="16.5748"><time>2024-09-03T07:01:16Z</time></trkpt>
                      <trkpt lat="51.8427" lon="16.5864"><time>2024-09-03T07:19:42Z</time></trkpt>
                      <trkpt lat="51.8490" lon="16.5932"><time>2024-09-03T07:33:58Z</time></trkpt>
                    </trkseg>
                  </trk>
                </gpx>
                """;

        var parsed = parserService.parse(gpx.getBytes(StandardCharsets.UTF_8));

        assertThat(parsed.getRouteName()).isEqualTo("Morning route");
        assertThat(parsed.getPointCount()).isEqualTo(3);
        assertThat(parsed.getDistanceKm()).isNotNull();
        assertThat(parsed.getDistanceKm()).isGreaterThan(0.0);
        assertThat(parsed.getDurationMinutes()).isEqualTo(32L);
        assertThat(parsed.getStartedAt()).isNotNull();
        assertThat(parsed.getPreviewPoints()).hasSize(3);
    }

    @Test
    void parseShouldUseRoutePointsWhenTrackMissing() {
        var gpx = """
                <gpx version="1.1" creator="allterra-tests" xmlns="http://www.topografix.com/GPX/1/1">
                  <rte>
                    <name>Route points only</name>
                    <rtept lat="10.0" lon="20.0"/>
                    <rtept lat="11.0" lon="21.0"/>
                  </rte>
                </gpx>
                """;

        var parsed = parserService.parse(gpx.getBytes(StandardCharsets.UTF_8));

        assertThat(parsed.getRouteName()).isEqualTo("Route points only");
        assertThat(parsed.getPointCount()).isEqualTo(2);
        assertThat(parsed.getPreviewPoints()).hasSize(2);
    }

    @Test
    void parseShouldFailWhenLessThanTwoPoints() {
        var gpx = """
                <gpx version="1.1" creator="allterra-tests" xmlns="http://www.topografix.com/GPX/1/1">
                  <trk>
                    <trkseg>
                      <trkpt lat="51.8402" lon="16.5748"/>
                    </trkseg>
                  </trk>
                </gpx>
                """;

        assertThatThrownBy(() -> parserService.parse(gpx.getBytes(StandardCharsets.UTF_8)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("at least 2 points");
    }
}

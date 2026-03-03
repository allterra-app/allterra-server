package com.allterra.server.model.route.gpx;

import com.allterra.server.model.route.dto.RoutePointDto;
import io.jenetics.jpx.GPX;
import io.jenetics.jpx.Track;
import io.jenetics.jpx.TrackSegment;
import io.jenetics.jpx.WayPoint;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Parses GPX payload and calculates route metrics.
 */
@Service
public class GpxRouteParserService {
    private static final int MAX_PREVIEW_POINTS = 300;

    /**
     * Parses GPX bytes and returns computed route metrics for persistence.
     *
     * @param gpxBytes GPX binary content
     * @return parsed route data
     */
    public ParsedRouteData parse(final byte[] gpxBytes) {
        if (gpxBytes == null || gpxBytes.length == 0) {
            throw new IllegalArgumentException("GPX file content is required");
        }

        final GPX gpx = readGpx(gpxBytes);
        final List<WayPoint> points = extractPoints(gpx);
        if (points.size() < 2) {
            throw new IllegalArgumentException("GPX track should contain at least 2 points");
        }

        final List<RoutePointDto> allPoints = points.stream()
                .map(point -> RoutePointDto.builder()
                        .lat(point.getLatitude().doubleValue())
                        .lon(point.getLongitude().doubleValue())
                        .build())
                .toList();

        final double distanceMeters = calculateDistanceMeters(allPoints);
        final Double distanceKm = roundToTwoDecimals(distanceMeters / 1000.0);

        final List<Instant> times = points.stream()
                .map(WayPoint::getTime)
                .flatMap(Optional::stream)
                .toList();
        final Long durationMinutes = calculateDurationMinutes(times);
        final LocalDateTime startedAt = times.isEmpty() ? null : LocalDateTime.ofInstant(times.get(0), ZoneOffset.UTC);

        return ParsedRouteData.builder()
                .routeName(extractRouteName(gpx))
                .distanceKm(distanceKm)
                .durationMinutes(durationMinutes)
                .pointCount(allPoints.size())
                .startedAt(startedAt)
                .previewPoints(samplePoints(allPoints, MAX_PREVIEW_POINTS))
                .build();
    }

    private GPX readGpx(final byte[] gpxBytes) {
        try (var inputStream = new ByteArrayInputStream(gpxBytes)) {
            return GPX.Reader.DEFAULT.read(inputStream);
        } catch (Exception exception) {
            throw new IllegalArgumentException("Uploaded file is not a valid GPX");
        }
    }

    private List<WayPoint> extractPoints(final GPX gpx) {
        final List<WayPoint> points = new ArrayList<>();

        for (Track track : gpx.getTracks()) {
            for (TrackSegment segment : track.getSegments()) {
                points.addAll(segment.getPoints());
            }
        }

        if (!points.isEmpty()) {
            return points;
        }

        gpx.getRoutes().forEach(route -> points.addAll(route.getPoints()));
        return points;
    }

    private String extractRouteName(final GPX gpx) {
        for (Track track : gpx.getTracks()) {
            final String name = track.getName().orElse(null);
            if (name != null && !name.isBlank()) {
                return name.trim();
            }
        }

        for (var route : gpx.getRoutes()) {
            final String name = route.getName().orElse(null);
            if (name != null && !name.isBlank()) {
                return name.trim();
            }
        }

        return gpx.getMetadata()
                .flatMap(metadata -> metadata.getName())
                .map(String::trim)
                .filter(name -> !name.isBlank())
                .orElse(null);
    }

    private Long calculateDurationMinutes(final List<Instant> times) {
        if (times.size() < 2) {
            return null;
        }
        final long seconds = times.get(times.size() - 1).getEpochSecond() - times.get(0).getEpochSecond();
        if (seconds < 0) {
            return null;
        }
        return seconds / 60;
    }

    private double calculateDistanceMeters(final List<RoutePointDto> points) {
        double distance = 0.0;
        for (int index = 0; index < points.size() - 1; index++) {
            distance += haversineMeters(points.get(index), points.get(index + 1));
        }
        return distance;
    }

    private double haversineMeters(final RoutePointDto a, final RoutePointDto b) {
        final double radius = 6_371_000.0;
        final double lat1 = toRadians(a.getLat());
        final double lat2 = toRadians(b.getLat());
        final double dLat = toRadians(b.getLat() - a.getLat());
        final double dLon = toRadians(b.getLon() - a.getLon());

        final double x = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(lat1) * Math.cos(lat2) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        final double c = 2 * Math.atan2(Math.sqrt(x), Math.sqrt(1 - x));
        return radius * c;
    }

    private double toRadians(final double value) {
        return value * (Math.PI / 180.0);
    }

    private Double roundToTwoDecimals(final double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private List<RoutePointDto> samplePoints(final List<RoutePointDto> points, final int maxPoints) {
        if (points.size() <= maxPoints) {
            return points;
        }

        final double step = (double) (points.size() - 1) / (double) (maxPoints - 1);
        final List<RoutePointDto> sampled = new ArrayList<>(maxPoints);
        for (int index = 0; index < maxPoints; index++) {
            int sourceIndex = (int) Math.round(index * step);
            sourceIndex = Math.max(0, Math.min(points.size() - 1, sourceIndex));
            sampled.add(points.get(sourceIndex));
        }
        return sampled;
    }
}

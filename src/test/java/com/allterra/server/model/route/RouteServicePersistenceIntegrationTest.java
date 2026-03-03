package com.allterra.server.model.route;

import com.allterra.server.media.service.MediaFileService;
import com.allterra.server.model.route.dto.request.RouteCreateRequestDto;
import com.allterra.server.model.user.SubscriptionPlan;
import com.allterra.server.model.user.User;
import com.allterra.server.model.user.UserRepository;
import com.allterra.server.model.user.UserRole;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class RouteServicePersistenceIntegrationTest {

    @Autowired
    private RouteService routeService;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private MediaFileService mediaFileService;

    @AfterEach
    void cleanup() {
        routeRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void createForUserShouldPersistRouteInDatabaseAndLoadAfterPersistenceContextReset() {
        var user = userRepository.save(
                User.builder()
                        .email("route-user@allterra.com")
                        .password("password")
                        .roles(Set.of(UserRole.USER))
                        .subscriptionPlan(SubscriptionPlan.FREE)
                        .build()
        );

        var createRequest = RouteCreateRequestDto.builder()
                .title("Morning route")
                .description("By river")
                .gpxFileId(uploadGpx("morning.gpx", "route-user@allterra.com"))
                .build();

        routeService.createForUser(user.getId(), createRequest, user.getEmail(), false);
        routeRepository.flush();
        entityManager.clear();

        var routes = routeService.getAllForUser(user.getId());

        assertThat(routeRepository.count()).isEqualTo(1);
        assertThat(routes).hasSize(1);
        final var firstRoute = routes.get(0);
        assertThat(firstRoute.getTitle()).isEqualTo("Morning route");
        assertThat(firstRoute.getGpxFileId()).isNotNull();
        assertThat(firstRoute.getPointCount()).isGreaterThanOrEqualTo(2);
        assertThat(firstRoute.getPreviewPoints()).isNotEmpty();
        assertThat(firstRoute.getUserId()).isEqualTo(user.getId());
    }

    @Test
    void deleteForUserShouldDeleteOnlyOwnedRoute() {
        var owner = userRepository.save(
                User.builder()
                        .email("route-owner@allterra.com")
                        .password("password")
                        .roles(Set.of(UserRole.USER))
                        .subscriptionPlan(SubscriptionPlan.FREE)
                        .build()
        );
        var other = userRepository.save(
                User.builder()
                        .email("route-other@allterra.com")
                        .password("password")
                        .roles(Set.of(UserRole.USER))
                        .subscriptionPlan(SubscriptionPlan.FREE)
                        .build()
        );

        var createRequest = RouteCreateRequestDto.builder()
                .title("Private route")
                .gpxFileId(uploadGpx("private.gpx", owner.getEmail()))
                .build();
        var created = routeService.createForUser(owner.getId(), createRequest, owner.getEmail(), false);

        assertThatThrownBy(() -> routeService.deleteForUser(other.getId(), created.getId()))
                .hasMessageContaining("not found for user");

        routeService.deleteForUser(owner.getId(), created.getId());

        assertThat(routeRepository.findById(created.getId())).isEmpty();
    }

    private java.util.UUID uploadGpx(final String fileName, final String ownerEmail) {
        final String gpx = """
                <gpx version="1.1" creator="allterra-tests" xmlns="http://www.topografix.com/GPX/1/1">
                  <trk>
                    <name>Sample route</name>
                    <trkseg>
                      <trkpt lat="51.8402" lon="16.5748"><time>2024-09-03T07:01:16Z</time></trkpt>
                      <trkpt lat="51.8427" lon="16.5864"><time>2024-09-03T07:19:42Z</time></trkpt>
                      <trkpt lat="51.8490" lon="16.5932"><time>2024-09-03T07:33:58Z</time></trkpt>
                    </trkseg>
                  </trk>
                </gpx>
                """;
        return mediaFileService.upload(
                fileName,
                "application/gpx+xml",
                gpx.getBytes(java.nio.charset.StandardCharsets.UTF_8),
                ownerEmail
        ).getId();
    }
}

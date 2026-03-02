package com.allterra.server.model.route;

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
                .gpxContent("<gpx><trk><trkseg><trkpt lat=\"10\" lon=\"20\"/></trkseg></trk></gpx>")
                .distanceKm(7.4)
                .durationMinutes(48L)
                .pointCount(1)
                .build();

        routeService.createForUser(user.getId(), createRequest);
        routeRepository.flush();
        entityManager.clear();

        var routes = routeService.getAllForUser(user.getId());

        assertThat(routeRepository.count()).isEqualTo(1);
        assertThat(routes).hasSize(1);
        assertThat(routes.getFirst().getTitle()).isEqualTo("Morning route");
        assertThat(routes.getFirst().getGpxContent()).contains("<gpx>");
        assertThat(routes.getFirst().getUserId()).isEqualTo(user.getId());
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
                .gpxContent("<gpx><trk><trkseg><trkpt lat=\"10\" lon=\"20\"/></trkseg></trk></gpx>")
                .build();
        var created = routeService.createForUser(owner.getId(), createRequest);

        assertThatThrownBy(() -> routeService.deleteForUser(other.getId(), created.getId()))
                .hasMessageContaining("not found for user");

        routeService.deleteForUser(owner.getId(), created.getId());

        assertThat(routeRepository.findById(created.getId())).isEmpty();
    }
}

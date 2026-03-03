package com.allterra.server.api;

import com.allterra.server.authentication.JwtTokenProvider;
import com.allterra.server.authentication.dto.JwtAuthenticationRequestDto;
import com.allterra.server.authentication.exception.InvalidCredentialsException;
import com.allterra.server.authentication.service.AuthService;
import com.allterra.server.exception.ResourceNotFoundException;
import com.allterra.server.model.poi.PoiService;
import com.allterra.server.model.poi.dto.PoiResponseDto;
import com.allterra.server.model.poi.dto.request.PoiCreateRequestDto;
import com.allterra.server.model.post.PostService;
import com.allterra.server.model.post.dto.PostResponseDto;
import com.allterra.server.model.post.dto.request.PostCreateRequestDto;
import com.allterra.server.model.route.RouteService;
import com.allterra.server.model.route.dto.RoutePointDto;
import com.allterra.server.model.route.dto.RouteResponseDto;
import com.allterra.server.model.route.dto.request.RouteCreateRequestDto;
import com.allterra.server.model.user.UserRole;
import com.allterra.server.photo.service.UserPhotoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ApiContractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private AuthService authService;

    @MockBean
    private UserPhotoService userPhotoService;

    @MockBean
    private RouteService routeService;

    @MockBean
    private PoiService poiService;

    @MockBean
    private PostService postService;

    @Test
    void loginShouldReturn401AndErrorPayloadWhenCredentialsAreInvalid() throws Exception {
        when(authService.authenticateUser(any(JwtAuthenticationRequestDto.class)))
                .thenThrow(new InvalidCredentialsException("Invalid email or password"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "missing@allterra.com",
                                  "password": "wrong"
                                }
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Invalid email or password"))
                .andExpect(jsonPath("$.path").value("/auth/login"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.validationErrors").isMap());
    }

    @Test
    void refreshShouldReturn400WhenRequestBodyIsMalformed() throws Exception {
        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").value("/auth/refresh"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.validationErrors").isMap());
    }

    @Test
    void adminEndpointShouldReturn401AndErrorPayloadWhenTokenMissing() throws Exception {
        mockMvc.perform(get("/admin/status"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Authentication is required to access this resource."))
                .andExpect(jsonPath("$.path").value("/admin/status"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.validationErrors").isMap());
    }

    @Test
    void adminEndpointShouldReturn403AndErrorPayloadWhenRoleIsUser() throws Exception {
        mockJwt("user-token", Set.of(UserRole.USER));

        mockMvc.perform(get("/admin/status")
                        .header("Authorization", "Bearer user-token"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.code").value(403))
                .andExpect(jsonPath("$.error").value("Forbidden"))
                .andExpect(jsonPath("$.message").value("You do not have permission to access this resource."))
                .andExpect(jsonPath("$.path").value("/admin/status"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.validationErrors").isMap());
    }

    @Test
    void adminEndpointShouldReturn200WhenRoleIsAdmin() throws Exception {
        mockJwt("admin-token", Set.of(UserRole.ADMIN));

        mockMvc.perform(get("/admin/status")
                        .header("Authorization", "Bearer admin-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ok"))
                .andExpect(jsonPath("$.scope").value("admin"));
    }

    @Test
    void userPhotoEndpointShouldReturn404AndErrorPayloadWhenEntityMissing() throws Exception {
        mockJwt("user-token", Set.of(UserRole.USER));
        when(userPhotoService.get(com.allterra.server.TestUuids.id(11)))
                .thenThrow(new ResourceNotFoundException("Photo not found with id: " + com.allterra.server.TestUuids.id(11)));

        mockMvc.perform(get("/user-photos/{id}", com.allterra.server.TestUuids.id(11))
                        .header("Authorization", "Bearer user-token"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Photo not found with id: " + com.allterra.server.TestUuids.id(11)))
                .andExpect(jsonPath("$.path").value("/user-photos/" + com.allterra.server.TestUuids.id(11)))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.validationErrors").isMap());
    }

    @Test
    void unsupportedContentTypeShouldReturn415WithErrorPayload() throws Exception {
        mockJwt("user-token", Set.of(UserRole.USER));

        mockMvc.perform(post("/user-photos")
                        .header("Authorization", "Bearer user-token")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("{}"))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.code").value(415))
                .andExpect(jsonPath("$.error").value("Unsupported Media Type"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").value("/user-photos"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.validationErrors").isMap());
    }

    @Test
    void routeCreateForUserShouldAcceptJsonWithCharset() throws Exception {
        mockJwt("user-token", Set.of(UserRole.USER));
        var userId = com.allterra.server.TestUuids.id(20);
        var response = RouteResponseDto.builder()
                .id(com.allterra.server.TestUuids.id(21))
                .title("Route")
                .gpxFileId(com.allterra.server.TestUuids.id(22))
                .previewPoints(java.util.List.of(RoutePointDto.builder().lat(1.0).lon(1.0).build()))
                .build();
        when(routeService.createForUser(
                org.mockito.ArgumentMatchers.eq(userId),
                any(RouteCreateRequestDto.class),
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.anyBoolean()
        ))
                .thenReturn(response);

        mockMvc.perform(post("/routes/users/{userId}", userId)
                        .header("Authorization", "Bearer user-token")
                        .contentType(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .content("""
                                {
                                  "title": "Route",
                                  "description": "Desc",
                                  "gpxFileId": "00000000-0000-0000-0000-000000000123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Route"));
    }

    @Test
    void poiCreateForUserShouldAcceptJsonWithCharset() throws Exception {
        mockJwt("user-token", Set.of(UserRole.USER));
        var userId = com.allterra.server.TestUuids.id(30);
        var response = PoiResponseDto.builder()
                .id(com.allterra.server.TestUuids.id(31))
                .name("Cafe")
                .build();
        when(poiService.createPoiForUser(org.mockito.ArgumentMatchers.eq(userId), any(PoiCreateRequestDto.class)))
                .thenReturn(response);

        mockMvc.perform(post("/pois/users/{userId}", userId)
                        .header("Authorization", "Bearer user-token")
                        .contentType(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .content("""
                                {
                                  "name": "Cafe",
                                  "description": "desc",
                                  "type": "SHOP",
                                  "actual": true,
                                  "rating": 0
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Cafe"));
    }

    @Test
    void postCreateForUserShouldAcceptJsonWithCharset() throws Exception {
        mockJwt("user-token", Set.of(UserRole.USER));
        var userId = com.allterra.server.TestUuids.id(40);
        var response = PostResponseDto.builder()
                .id(com.allterra.server.TestUuids.id(41))
                .title("Morning ride")
                .body("Short description")
                .build();
        when(postService.createPostForUser(org.mockito.ArgumentMatchers.eq(userId), any(PostCreateRequestDto.class)))
                .thenReturn(response);

        mockMvc.perform(post("/posts/users/{userId}", userId)
                        .header("Authorization", "Bearer user-token")
                        .contentType(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .content("""
                                {
                                  "title": "Morning ride",
                                  "body": "Short description"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Morning ride"));
    }

    private void mockJwt(final String token, final Set<UserRole> roles) {
        when(jwtTokenProvider.validateToken(token)).thenReturn(JwtTokenProvider.ValidateTokenStatus.VALID);
        when(jwtTokenProvider.getUserEmailFromToken(token)).thenReturn("user@allterra.com");
        when(jwtTokenProvider.getUserRolesFromToken(token)).thenReturn(roles);
    }
}

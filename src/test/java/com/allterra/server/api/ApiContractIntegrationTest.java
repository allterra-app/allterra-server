package com.allterra.server.api;

import com.allterra.server.authentication.JwtTokenProvider;
import com.allterra.server.authentication.dto.JwtAuthenticationRequestDto;
import com.allterra.server.authentication.exception.InvalidCredentialsException;
import com.allterra.server.authentication.service.AuthService;
import com.allterra.server.exception.ResourceNotFoundException;
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

    private void mockJwt(final String token, final Set<UserRole> roles) {
        when(jwtTokenProvider.validateToken(token)).thenReturn(JwtTokenProvider.ValidateTokenStatus.VALID);
        when(jwtTokenProvider.getUserEmailFromToken(token)).thenReturn("user@allterra.com");
        when(jwtTokenProvider.getUserRolesFromToken(token)).thenReturn(roles);
    }
}

package com.allterra.server.authentication;

import com.allterra.server.exception.ApiErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Handles unauthorized requests.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final AuthenticationException authException
    ) throws IOException {
        log.debug("Unauthorized access: {}", authException.getMessage());

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        objectMapper.writeValue(
                response.getWriter(),
                ApiErrorResponse.of(
                        HttpStatus.UNAUTHORIZED,
                        request.getAttribute("auth_error_message") == null
                                ? "Authentication is required to access this resource."
                                : request.getAttribute("auth_error_message").toString(),
                        request.getRequestURI()
                )
        );
    }
}

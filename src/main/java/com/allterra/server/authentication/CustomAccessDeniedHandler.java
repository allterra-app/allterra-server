package com.allterra.server.authentication;

import com.allterra.server.exception.ApiErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Authentication denied handler.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final AccessDeniedException accessDeniedException
    ) throws IOException {
        log.debug("Access Denied: {}", accessDeniedException.getMessage());
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        objectMapper.writeValue(
                response.getWriter(),
                ApiErrorResponse.of(
                        HttpStatus.FORBIDDEN,
                        "You do not have permission to access this resource.",
                        request.getRequestURI()
                )
        );
    }
}

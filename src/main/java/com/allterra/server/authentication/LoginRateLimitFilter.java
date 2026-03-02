package com.allterra.server.authentication;

import com.allterra.server.exception.ApiErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Rate-limits login attempts per client IP.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LoginRateLimitFilter extends OncePerRequestFilter {
    private static final String LOGIN_PATH = "/auth/login";

    private final ObjectMapper objectMapper;
    private final ConcurrentHashMap<String, ConcurrentLinkedDeque<Long>> requestsByIp = new ConcurrentHashMap<>();

    @Value("${security.rate-limit.login.max-attempts:10}")
    private int maxAttempts;

    @Value("${security.rate-limit.login.window-seconds:60}")
    private long windowSeconds;

    @Override
    protected boolean shouldNotFilter(final HttpServletRequest request) {
        return !"POST".equalsIgnoreCase(request.getMethod()) || !LOGIN_PATH.equals(request.getServletPath());
    }

    @Override
    protected void doFilterInternal(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final FilterChain filterChain
    ) throws ServletException, IOException {
        final var clientIp = resolveClientIp(request);
        final var nowMillis = Instant.now().toEpochMilli();
        final var windowMillis = windowSeconds * 1000L;

        final var attempts = requestsByIp.computeIfAbsent(clientIp, key -> new ConcurrentLinkedDeque<>());
        synchronized (attempts) {
            while (!attempts.isEmpty() && nowMillis - attempts.peekFirst() > windowMillis) {
                attempts.pollFirst();
            }

            if (attempts.size() >= maxAttempts) {
                log.warn("Rate limit exceeded for login endpoint from ip [{}]", clientIp);
                writeRateLimitResponse(request, response);
                return;
            }
            attempts.addLast(nowMillis);
        }

        filterChain.doFilter(request, response);
    }

    private void writeRateLimitResponse(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        objectMapper.writeValue(
                response.getWriter(),
                ApiErrorResponse.of(
                        HttpStatus.TOO_MANY_REQUESTS,
                        "Too many login attempts. Please try again later.",
                        request.getRequestURI()
                )
        );
    }

    private String resolveClientIp(final HttpServletRequest request) {
        final var forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            final var first = forwardedFor.split(",")[0].trim();
            if (!first.isBlank()) {
                return first;
            }
        }
        return request.getRemoteAddr() == null ? "unknown" : request.getRemoteAddr();
    }
}

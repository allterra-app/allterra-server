package com.allterra.server.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class LoginRateLimitFilterTest {

    @Test
    void shouldReturnTooManyRequestsWhenLimitExceeded() throws Exception {
        var filter = new LoginRateLimitFilter(createObjectMapper());
        ReflectionTestUtils.setField(filter, "maxAttempts", 2);
        ReflectionTestUtils.setField(filter, "windowSeconds", 60L);

        var first = perform(filter);
        var second = perform(filter);
        var third = perform(filter);

        assertThat(first.getStatus()).isEqualTo(200);
        assertThat(second.getStatus()).isEqualTo(200);
        assertThat(third.getStatus()).isEqualTo(429);
        assertThat(third.getContentAsString()).contains("Too many login attempts");
    }

    @Test
    void shouldSkipNonLoginEndpoints() throws Exception {
        var filter = new LoginRateLimitFilter(createObjectMapper());
        ReflectionTestUtils.setField(filter, "maxAttempts", 1);
        ReflectionTestUtils.setField(filter, "windowSeconds", 60L);

        var request = new MockHttpServletRequest("GET", "/posts");
        request.setServletPath("/posts");
        var response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertThat(response.getStatus()).isEqualTo(200);
    }

    private MockHttpServletResponse perform(final LoginRateLimitFilter filter) throws Exception {
        var request = new MockHttpServletRequest("POST", "/auth/login");
        request.setServletPath("/auth/login");
        request.setRemoteAddr("127.0.0.1");
        var response = new MockHttpServletResponse();
        filter.doFilter(request, response, new MockFilterChain());
        return response;
    }

    private ObjectMapper createObjectMapper() {
        return new ObjectMapper().findAndRegisterModules();
    }
}

package com.allterra.server.config;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.TimeZone;

@TestConfiguration
public class TimeTestConfig {

    public static final String FIXED_TIMESTAMP = "2024-10-10T10:00:01Z";

    @PostConstruct
    public void setDefaultTimeZone() {
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneOffset.UTC));
    }

    @Bean
    public Clock clock() {
        return Clock.fixed(Instant.parse(FIXED_TIMESTAMP), ZoneOffset.UTC);
    }
}

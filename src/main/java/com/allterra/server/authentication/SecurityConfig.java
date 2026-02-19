package com.allterra.server.authentication;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Authentication configuration.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SecurityConfig {

    JwtTokenProvider jwtTokenProvider;

    CustomAccessDeniedHandler customAccessDeniedHandler;
    CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    /**
     * Configures the security filter chain for HTTP requests.
     * <p>
     * Disables CSRF protection, sets up exception handling for access denied errors,
     * and configures authentication and authorization rules. The method also ensures
     * stateless session management and adds a JWT filter before the
     * {@link UsernamePasswordAuthenticationFilter} to handle JWT token validation.
     * </p>
     *
     * @param http the {@link HttpSecurity} instance to configure
     * @return the configured {@link SecurityFilterChain}
     * @throws Exception if an error occurs while configuring the security filter chain
     */
    @Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {
        http.exceptionHandling(exception -> exception
                        .accessDeniedHandler(customAccessDeniedHandler)
                        .authenticationEntryPoint(customAuthenticationEntryPoint))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/users/*/roles").hasRole("ADMIN")
                        .anyRequest().hasAnyRole("USER", "ADMIN")
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(new JwtTokenFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Creates a {@link PasswordEncoder} bean using {@link BCryptPasswordEncoder}.
     * This encoder provides a secure hashing mechanism for passwords using the BCrypt algorithm.
     *
     * @return a {@link PasswordEncoder} instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Creates an {@link AuthenticationManager} bean using the provided {@link AuthenticationConfiguration}.
     * This manager is responsible for authenticating users based on the configured authentication mechanism.
     *
     * @param authenticationConfiguration the {@link AuthenticationConfiguration} to obtain the authentication manager
     * @return the configured {@link AuthenticationManager}
     * @throws Exception if an error occurs while creating the authentication manager
     */
    @Bean
    public AuthenticationManager authenticationManager(final AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}

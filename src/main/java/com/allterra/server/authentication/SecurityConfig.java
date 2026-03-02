package com.allterra.server.authentication;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
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
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.ForwardedHeaderFilter;

/**
 * Authentication configuration.
 */
@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class SecurityConfig {
    private static final String AUTH_LOGIN_PATH = "/auth/login";
    private static final String AUTH_REGISTER_PATH = "/auth/register";
    private static final String AUTH_REFRESH_PATH = "/auth/refresh";

    JwtTokenProvider jwtTokenProvider;

    CustomAccessDeniedHandler customAccessDeniedHandler;
    CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    LoginRateLimitFilter loginRateLimitFilter;

    @NonFinal
    @Value("${security.require-https:true}")
    boolean requireHttps;

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
    public SecurityFilterChain securityFilterChain(
            final HttpSecurity http,
            final JwtTokenFilter jwtTokenFilter
    ) throws Exception {
        if (requireHttps) {
            http.requiresChannel(channel -> channel.anyRequest().requiresSecure());
        }

        http.exceptionHandling(exception -> exception
                        .accessDeniedHandler(customAccessDeniedHandler)
                        .authenticationEntryPoint(customAuthenticationEntryPoint))
                // CSRF is safe to disable here because API is stateless JWT-based and does not use cookie auth.
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, AUTH_LOGIN_PATH).permitAll()
                        .requestMatchers(HttpMethod.POST, AUTH_REGISTER_PATH).permitAll()
                        .requestMatchers(HttpMethod.POST, AUTH_REFRESH_PATH).permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/users/*/roles").hasRole("ADMIN")
                        .anyRequest().hasAnyRole("USER", "ADMIN")
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(loginRateLimitFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Builds matcher for endpoints that must stay publicly accessible without JWT.
     *
     * @return matcher for public authentication routes
     */
    @Bean
    public RequestMatcher publicEndpointsMatcher() {
        return new OrRequestMatcher(
                new AntPathRequestMatcher(AUTH_LOGIN_PATH, HttpMethod.POST.name()),
                new AntPathRequestMatcher(AUTH_REGISTER_PATH, HttpMethod.POST.name()),
                new AntPathRequestMatcher(AUTH_REFRESH_PATH, HttpMethod.POST.name())
        );
    }

    /**
     * Creates JWT authentication filter configured with explicit public endpoint matcher.
     *
     * @param publicEndpointsMatcher matcher for endpoints that must bypass JWT filter
     * @return configured JWT token filter
     */
    @Bean
    public JwtTokenFilter jwtTokenFilter(final RequestMatcher publicEndpointsMatcher) {
        return new JwtTokenFilter(jwtTokenProvider, publicEndpointsMatcher);
    }

    /**
     * Enables processing of reverse-proxy forwarded headers (proto/host) for secure channel enforcement.
     *
     * @return forwarded header filter
     */
    @Bean
    public ForwardedHeaderFilter forwardedHeaderFilter() {
        return new ForwardedHeaderFilter();
    }

    /**
     * Registers {@link ForwardedHeaderFilter} with high priority so channel security sees forwarded scheme.
     *
     * @param forwardedHeaderFilter forwarded header filter bean
     * @return forwarded header filter registration bean
     */
    @Bean
    public FilterRegistrationBean<ForwardedHeaderFilter> forwardedHeaderFilterRegistration(
            final ForwardedHeaderFilter forwardedHeaderFilter
    ) {
        var registration = new FilterRegistrationBean<>(forwardedHeaderFilter);
        registration.setOrder(0);
        return registration;
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

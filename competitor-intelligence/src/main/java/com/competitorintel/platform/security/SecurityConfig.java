package com.competitorintel.platform.security;

import com.competitorintel.platform.security.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Stateless JWT-based Spring Security configuration.
 *
 * PUBLIC_PATHS are listed explicitly so they are always evaluated FIRST,
 * before any role-based matcher.  The auth endpoints must be permit-all
 * so that the login / refresh calls work without a token.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    /**
     * Endpoints that are publicly accessible without a JWT.
     * Both /api/v1/auth/** and /api/auth/** are included so the frontend
     * works regardless of whether the /v1 prefix is used.
     */
    private static final String[] PUBLIC_PATHS = {
            // ── Authentication ───────────────────────────────────────────
            "/api/v1/auth/**",
            "/api/auth/**",
            // ── Swagger / OpenAPI ────────────────────────────────────────
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**",
            "/api-docs/**",
            "/api-docs",
            // ── Actuator (health probe only) ─────────────────────────────
            "/actuator/health",
            "/actuator/info",
            // ── H2 console (dev only) ────────────────────────────────────
            "/h2-console/**",
            // ── Spring error page ────────────────────────────────────────
            "/error",
            "/"
    };

    private final UserDetailsServiceImpl  userDetailsService;
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final JwtAuthEntryPoint       jwtAuthEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(AbstractHttpConfigurer::disable)
            // Allow H2 console to render in an iframe (dev only)
            .headers(h -> h.frameOptions(fo -> fo.sameOrigin()))
            .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthEntryPoint))
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                    // Public endpoints — MUST come before any role-based matcher
                    .requestMatchers(PUBLIC_PATHS).permitAll()
                    // All OPTIONS pre-flight requests are public
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    // Protected API — role-based
                    .requestMatchers(HttpMethod.GET,    "/api/v1/**").hasAnyRole("ADMIN", "ANALYST", "VIEWER")
                    .requestMatchers(HttpMethod.POST,   "/api/v1/**").hasAnyRole("ADMIN", "ANALYST")
                    .requestMatchers(HttpMethod.PUT,    "/api/v1/**").hasAnyRole("ADMIN", "ANALYST")
                    .requestMatchers(HttpMethod.PATCH,  "/api/v1/**").hasAnyRole("ADMIN", "ANALYST")
                    .requestMatchers(HttpMethod.DELETE, "/api/v1/**").hasRole("ADMIN")
                    .anyRequest().authenticated()
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOriginPatterns(List.of("*"));
        cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        cfg.setAllowedHeaders(List.of("*"));
        cfg.setExposedHeaders(List.of("Authorization", "X-Total-Count", "X-Page-Number", "X-Page-Size"));
        cfg.setAllowCredentials(true);
        cfg.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }
}

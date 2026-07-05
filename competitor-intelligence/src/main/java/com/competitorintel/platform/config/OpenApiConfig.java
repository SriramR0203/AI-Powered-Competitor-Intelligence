package com.competitorintel.platform.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    private static final String BEARER_AUTH = "bearerAuth";

    /**
     * The public-facing base URL for this API.
     * Set API_BASE_URL in production to the Render service URL, e.g.:
     *   https://competitor-intel-api.onrender.com
     * Falls back to a relative root ("/") so Swagger UI works on any host
     * without knowing the exact URL at build time.
     */
    @Value("${API_BASE_URL:}")
    private String apiBaseUrl;

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI competitorIntelOpenAPI() {
        // When API_BASE_URL is set (i.e. running on Render), only expose that
        // server entry.  Adding a localhost entry alongside it is misleading —
        // requests sent from the Render-hosted Swagger UI to localhost:8080 go
        // nowhere.  When API_BASE_URL is absent (local dev), fall back to the
        // localhost entry so the UI still works out of the box.
        List<Server> servers = new java.util.ArrayList<>();
        if (apiBaseUrl != null && !apiBaseUrl.isBlank()) {
            servers.add(new Server().url(apiBaseUrl).description("Production"));
        } else {
            servers.add(new Server()
                    .url("http://localhost:" + serverPort)
                    .description("Local Dev"));
        }

        return new OpenAPI()
                .info(new Info()
                        .title("AI-Powered Competitor Intelligence Platform API")
                        .version("1.0.0")
                        .description("Enterprise API for automated competitor intelligence, AI enrichment, alerts, dashboards and analytics.")
                        .contact(new Contact()
                                .name("Competitor Intelligence Team")
                                .email("support@competitorintel.com"))
                        .license(new License().name("Enterprise License")))
                .servers(servers)
                .addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH))
                .components(new Components()
                        .addSecuritySchemes(BEARER_AUTH, new SecurityScheme()
                                .name(BEARER_AUTH)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Provide a valid JWT Bearer token from POST /api/v1/auth/login")));
    }
}

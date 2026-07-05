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

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI competitorIntelOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AI-Powered Competitor Intelligence Platform API")
                        .version("1.0.0")
                        .description("Enterprise API for automated competitor intelligence, AI enrichment, alerts, dashboards and analytics.")
                        .contact(new Contact()
                                .name("Competitor Intelligence Team")
                                .email("support@competitorintel.com"))
                        .license(new License().name("Enterprise License")))
                .servers(List.of(
                        new Server().url("http://localhost:" + serverPort)
                                    .description("Local Dev"),
                        new Server().url("https://api.competitorintel.com")
                                    .description("Production")))
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

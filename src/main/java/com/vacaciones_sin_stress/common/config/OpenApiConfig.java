package com.vacaciones_sin_stress.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI configuration for interactive auth headers in Swagger UI.
 */
@Configuration
public class OpenApiConfig {

    private static final String CLERK_USER_ID_SCHEME = "ClerkUserIdHeader";
    private static final String CLERK_EMAIL_SCHEME = "ClerkEmailHeader";

    /**
     * Exposes Clerk header auth schemes so Swagger UI can send them in requests.
     */
    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes(
                                CLERK_USER_ID_SCHEME,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.HEADER)
                                        .name("X-Clerk-User-Id")
                                        .description("Clerk user id (sub)")
                        )
                        .addSecuritySchemes(
                                CLERK_EMAIL_SCHEME,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.HEADER)
                                        .name("X-Clerk-Email")
                                        .description("Clerk user email")
                        ))
                .addSecurityItem(new SecurityRequirement().addList(CLERK_USER_ID_SCHEME))
                .addSecurityItem(new SecurityRequirement().addList(CLERK_EMAIL_SCHEME));
    }
}

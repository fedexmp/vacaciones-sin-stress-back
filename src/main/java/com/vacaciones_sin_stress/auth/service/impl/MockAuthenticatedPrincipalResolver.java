package com.vacaciones_sin_stress.auth.service.impl;

import com.vacaciones_sin_stress.auth.model.AuthenticatedPrincipal;
import com.vacaciones_sin_stress.auth.service.AuthenticatedPrincipalResolver;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Optional;

/**
 * Fallback resolver for local development when real auth is not wired yet.
 */
@Component
@Order(1000)
public class MockAuthenticatedPrincipalResolver implements AuthenticatedPrincipalResolver {

    @Value("${app.auth.mock-enabled:true}")
    private boolean mockEnabled;

    @Value("${app.auth.mock-current-user-email:sofia.employee@vacaciones.local}")
    private String mockCurrentUserEmail;

    @Value("${app.auth.mock-current-user-clerk-id:}")
    private String mockCurrentUserClerkId;

    /**
     * Resolves a configured mock principal when fallback mode is enabled.
     */
    @Override
    public Optional<AuthenticatedPrincipal> resolve(HttpServletRequest request) {
        if (!mockEnabled) {
            return Optional.empty();
        }

        String email = trimToNull(mockCurrentUserEmail);
        String clerkUserId = trimToNull(mockCurrentUserClerkId);
        if (!StringUtils.hasText(email) && !StringUtils.hasText(clerkUserId)) {
            return Optional.empty();
        }
        return Optional.of(new AuthenticatedPrincipal(clerkUserId, email));
    }

    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }
}

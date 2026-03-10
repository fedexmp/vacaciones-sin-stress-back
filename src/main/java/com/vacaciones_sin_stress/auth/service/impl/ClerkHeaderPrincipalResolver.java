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
 * Extracts Clerk identity from request headers.
 *
 * TODO: Replace/augment this with verified JWT claims when Clerk token validation is enabled.
 */
@Component
@Order(10)
public class ClerkHeaderPrincipalResolver implements AuthenticatedPrincipalResolver {

    @Value("${app.auth.clerk.user-id-header:X-Clerk-User-Id}")
    private String clerkUserIdHeader;

    @Value("${app.auth.clerk.email-header:X-Clerk-Email}")
    private String clerkEmailHeader;

    /**
     * Resolves principal from configured Clerk headers.
     */
    @Override
    public Optional<AuthenticatedPrincipal> resolve(HttpServletRequest request) {
        String clerkUserId = trimToNull(request.getHeader(clerkUserIdHeader));
        String email = trimToNull(request.getHeader(clerkEmailHeader));
        if (!StringUtils.hasText(clerkUserId) && !StringUtils.hasText(email)) {
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

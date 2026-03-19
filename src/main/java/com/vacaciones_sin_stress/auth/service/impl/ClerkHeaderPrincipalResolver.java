package com.vacaciones_sin_stress.auth.service.impl;

import com.vacaciones_sin_stress.auth.model.AuthenticatedPrincipal;
import com.vacaciones_sin_stress.auth.service.AuthenticatedPrincipalResolver;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Optional;

/**
 * Extracts Clerk identity from raw request headers (X-Clerk-User-Id / X-Clerk-Email).
 *
 * <p>Guarded by {@code app.auth.clerk.trust-headers}. When disabled, this resolver
 * is skipped so that only JWT-verified identities are accepted.</p>
 */
@Slf4j
@Component
@Order(10)
public class ClerkHeaderPrincipalResolver implements AuthenticatedPrincipalResolver {

    @Value("${app.auth.clerk.user-id-header:X-Clerk-User-Id}")
    private String clerkUserIdHeader;

    @Value("${app.auth.clerk.email-header:X-Clerk-Email}")
    private String clerkEmailHeader;

    @Value("${app.auth.clerk.trust-headers:true}")
    private boolean trustHeaders;

    /**
     * Resolves principal from configured Clerk headers when header trust is enabled.
     */
    @Override
    public Optional<AuthenticatedPrincipal> resolve(HttpServletRequest request) {
        if (!trustHeaders) {
            return Optional.empty();
        }
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

package com.vacaciones_sin_stress.auth.context;

import com.vacaciones_sin_stress.auth.model.AuthenticatedPrincipal;

import java.util.Optional;

/**
 * Holds request-scoped authentication data in a thread-local container.
 */
public final class AuthContextHolder {

    private static final ThreadLocal<AuthenticatedPrincipal> AUTH_CONTEXT = new ThreadLocal<>();

    private AuthContextHolder() {
    }

    public static void setPrincipal(AuthenticatedPrincipal principal) {
        AUTH_CONTEXT.set(principal);
    }

    public static Optional<AuthenticatedPrincipal> getPrincipal() {
        return Optional.ofNullable(AUTH_CONTEXT.get());
    }

    public static void clear() {
        AUTH_CONTEXT.remove();
    }
}

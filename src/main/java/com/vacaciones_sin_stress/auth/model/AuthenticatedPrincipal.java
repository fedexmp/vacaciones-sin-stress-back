package com.vacaciones_sin_stress.auth.model;

/**
 * Represents the authenticated identity extracted from request metadata or token claims.
 */
public class AuthenticatedPrincipal {

    private final String clerkUserId;
    private final String email;

    public AuthenticatedPrincipal(String clerkUserId, String email) {
        this.clerkUserId = clerkUserId;
        this.email = email;
    }

    public String getClerkUserId() {
        return clerkUserId;
    }

    public String getEmail() {
        return email;
    }
}

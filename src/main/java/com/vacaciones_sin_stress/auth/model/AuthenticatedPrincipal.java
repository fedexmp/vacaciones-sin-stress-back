package com.vacaciones_sin_stress.auth.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Represents the authenticated identity extracted from request metadata or token claims.
 */
@Getter
@RequiredArgsConstructor
public class AuthenticatedPrincipal {

    private final String clerkUserId;
    private final String email;
}

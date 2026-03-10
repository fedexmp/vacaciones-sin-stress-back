package com.vacaciones_sin_stress.auth.service;

import com.vacaciones_sin_stress.auth.model.AuthenticatedPrincipal;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Optional;

/**
 * Strategy interface for extracting authenticated identity from a request.
 */
public interface AuthenticatedPrincipalResolver {

    /**
     * Tries to extract identity from the request.
     *
     * @param request current HTTP request
     * @return resolved principal when available
     */
    Optional<AuthenticatedPrincipal> resolve(HttpServletRequest request);
}

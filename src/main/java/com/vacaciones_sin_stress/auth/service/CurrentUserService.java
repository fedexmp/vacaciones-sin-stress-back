package com.vacaciones_sin_stress.auth.service;

import com.vacaciones_sin_stress.user.entity.User;

/**
 * Provides the current user context without coupling business logic to a concrete auth provider.
 */
public interface CurrentUserService {

    /**
     * Resolves the current authenticated user for the request.
     *
     * @return current user
     */
    User getCurrentUser();
}

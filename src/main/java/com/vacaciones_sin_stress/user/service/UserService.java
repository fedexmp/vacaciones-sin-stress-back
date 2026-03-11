package com.vacaciones_sin_stress.user.service;

import com.vacaciones_sin_stress.user.dto.response.UserMeResponse;

/**
 * Provides user profile use cases.
 */
public interface UserService {

    /**
     * Returns profile of the current authenticated user.
     *
     * @return authenticated user profile
     */
    UserMeResponse getMe();
}

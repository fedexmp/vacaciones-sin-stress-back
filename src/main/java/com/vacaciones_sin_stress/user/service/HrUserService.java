package com.vacaciones_sin_stress.user.service;

import com.vacaciones_sin_stress.user.dto.response.UserSummaryResponse;

import java.util.List;

/**
 * HR-only operations related to users.
 */
public interface HrUserService {

    /**
     * Returns a summary list of all active users, sorted by name.
     * Requires the caller to be an HR user.
     *
     * @return active users ordered alphabetically
     */
    List<UserSummaryResponse> getAllActiveUsers();
}

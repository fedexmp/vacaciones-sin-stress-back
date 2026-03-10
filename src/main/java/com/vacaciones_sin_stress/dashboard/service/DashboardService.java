package com.vacaciones_sin_stress.dashboard.service;

import com.vacaciones_sin_stress.dashboard.dto.response.DashboardResponse;

/**
 * Provides dashboard data for current user.
 */
public interface DashboardService {

    /**
     * Returns dashboard summary for current user.
     *
     * @return dashboard data
     */
    DashboardResponse getMyDashboard();
}

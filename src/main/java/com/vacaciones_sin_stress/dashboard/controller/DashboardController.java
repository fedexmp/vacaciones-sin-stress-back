package com.vacaciones_sin_stress.dashboard.controller;

import com.vacaciones_sin_stress.dashboard.dto.response.DashboardResponse;
import com.vacaciones_sin_stress.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Exposes dashboard endpoint for current user.
 */
@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * Returns dashboard summary for current user.
     */
    @GetMapping("/me")
    public ResponseEntity<DashboardResponse> getMyDashboard() {
        return ResponseEntity.ok(dashboardService.getMyDashboard());
    }
}

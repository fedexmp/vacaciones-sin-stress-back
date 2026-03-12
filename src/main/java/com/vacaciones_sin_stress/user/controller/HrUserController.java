package com.vacaciones_sin_stress.user.controller;

import com.vacaciones_sin_stress.user.dto.response.UserSummaryResponse;
import com.vacaciones_sin_stress.user.service.HrUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * HR-only endpoints for user management.
 */
@RestController
@RequestMapping("/hr/users")
@RequiredArgsConstructor
public class HrUserController {

    private final HrUserService hrUserService;

    /**
     * Returns all active users sorted by name.
     * Used by the HR frontend for user-picker dropdowns (e.g. balance assignment).
     */
    @GetMapping
    public ResponseEntity<List<UserSummaryResponse>> getAllActiveUsers() {
        return ResponseEntity.ok(hrUserService.getAllActiveUsers());
    }
}

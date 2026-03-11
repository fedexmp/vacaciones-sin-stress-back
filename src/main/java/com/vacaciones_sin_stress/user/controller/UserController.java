package com.vacaciones_sin_stress.user.controller;

import com.vacaciones_sin_stress.user.dto.response.UserMeResponse;
import com.vacaciones_sin_stress.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Exposes authenticated user profile endpoints.
 */
@RestController
@RequestMapping({"/user"})
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Returns profile data for current authenticated user.
     */
    @GetMapping("/me")
    public ResponseEntity<UserMeResponse> getMe() {
        return ResponseEntity.ok(userService.getMe());
    }
}

package com.vacaciones_sin_stress.notification.controller;

import com.vacaciones_sin_stress.notification.dto.response.NotificationResponse;
import com.vacaciones_sin_stress.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Exposes notification endpoints for polling and viewed acknowledgment.
 */
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * Returns notifications relevant for the current user.
     */
    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getMyNotifications() {
        return ResponseEntity.ok(notificationService.getMyNotifications());
    }

    /**
     * Marks one final request result notification as viewed.
     */
    @PutMapping("/{requestId}/viewed")
    public ResponseEntity<Void> markAsViewed(@PathVariable("requestId") Long requestId) {
        notificationService.markAsViewed(requestId);
        return ResponseEntity.noContent().build();
    }
}

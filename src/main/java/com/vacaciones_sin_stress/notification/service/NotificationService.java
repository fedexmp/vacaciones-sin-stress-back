package com.vacaciones_sin_stress.notification.service;

import com.vacaciones_sin_stress.notification.dto.response.NotificationResponse;

import java.util.List;

/**
 * Exposes notification use cases for pending actions and unseen final outcomes.
 */
public interface NotificationService {

    /**
     * Returns notifications relevant for the current user.
     *
     * @return current user's notifications
     */
    List<NotificationResponse> getMyNotifications();

    /**
     * Marks a final request outcome notification as viewed by request owner.
     *
     * @param requestId time-off request id
     */
    void markAsViewed(Long requestId);
}

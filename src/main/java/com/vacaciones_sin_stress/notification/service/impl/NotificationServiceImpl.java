package com.vacaciones_sin_stress.notification.service.impl;

import com.vacaciones_sin_stress.auth.service.CurrentUserService;
import com.vacaciones_sin_stress.common.enums.Role;
import com.vacaciones_sin_stress.common.enums.TimeOffRequestStatus;
import com.vacaciones_sin_stress.common.exception.ConflictException;
import com.vacaciones_sin_stress.common.exception.ForbiddenOperationException;
import com.vacaciones_sin_stress.common.exception.ResourceNotFoundException;
import com.vacaciones_sin_stress.notification.dto.response.NotificationResponse;
import com.vacaciones_sin_stress.notification.enums.NotificationType;
import com.vacaciones_sin_stress.notification.mapper.NotificationMapper;
import com.vacaciones_sin_stress.notification.service.NotificationService;
import com.vacaciones_sin_stress.user.entity.User;
import com.vacaciones_sin_stress.user.repository.UserRepository;
import com.vacaciones_sin_stress.vacation.entity.TimeOffRequest;
import com.vacaciones_sin_stress.vacation.repository.TimeOffRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Resolves notifications from time-off request state and role-based pending actions.
 */
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private static final List<TimeOffRequestStatus> FINAL_STATUSES = List.of(
            TimeOffRequestStatus.APPROVED,
            TimeOffRequestStatus.REJECTED_LEADER,
            TimeOffRequestStatus.REJECTED_HR
    );

    private final TimeOffRequestRepository timeOffRequestRepository;
    private final CurrentUserService currentUserService;
    private final NotificationMapper notificationMapper;
    private final UserRepository userRepository;

    /**
     * Returns own unseen final outcomes plus pending actions for leader/hr roles.
     */
    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getMyNotifications() {
        User currentUser = currentUserService.getCurrentUser();
        List<NotificationResponse> notifications = new ArrayList<>();

        timeOffRequestRepository
                .findByUserIdAndStatusInAndNotifiedFalseOrderByUpdatedAtDesc(currentUser.getId(), FINAL_STATUSES)
                .stream()
                .map(this::toRequestResultNotification)
                .forEach(notifications::add);

        timeOffRequestRepository.findByStatusAndLeaderId(TimeOffRequestStatus.PENDING_LEADER, currentUser.getId())
                .stream()
                .map(this::toPendingActionNotification)
                .forEach(notifications::add);

        if (currentUser.getRole() == Role.HR) {
            timeOffRequestRepository.findByStatusOrderByRequestedAtAsc(TimeOffRequestStatus.PENDING_HR)
                    .stream()
                    .map(this::toPendingActionNotification)
                    .forEach(notifications::add);
        }

        return notifications.stream()
                .sorted(Comparator.comparing(NotificationServiceImpl::resolveSortDate, Comparator.reverseOrder()))
                .toList();
    }

    /**
     * Marks one final request notification as viewed by its owner.
     */
    @Override
    @Transactional
    public void markAsViewed(Long requestId) {
        User currentUser = currentUserService.getCurrentUser();
        TimeOffRequest timeOffRequest = timeOffRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Time-off request not found: " + requestId));

        if (!timeOffRequest.getUserId().equals(currentUser.getId())) {
            throw new ForbiddenOperationException("Only request owner can mark this notification as viewed");
        }

        if (!FINAL_STATUSES.contains(timeOffRequest.getStatus())) {
            throw new ConflictException("Only final requests can be marked as viewed");
        }

        if (timeOffRequest.isNotified()) {
            return;
        }

        timeOffRequest.setNotified(true);
        timeOffRequestRepository.save(timeOffRequest);
    }

    private NotificationResponse toPendingActionNotification(TimeOffRequest timeOffRequest) {
        NotificationResponse response = notificationMapper.toResponse(timeOffRequest);
        response.setType(NotificationType.PENDING_ACTION);
        response.setActionRequired(true);
        response.setViewed(false);
        enrichWithUser(response);
        return response;
    }

    private NotificationResponse toRequestResultNotification(TimeOffRequest timeOffRequest) {
        NotificationResponse response = notificationMapper.toResponse(timeOffRequest);
        response.setType(NotificationType.REQUEST_RESULT);
        response.setActionRequired(false);
        response.setViewed(timeOffRequest.isNotified());
        enrichWithUser(response);
        return response;
    }

    private void enrichWithUser(NotificationResponse response) {
        if (response.getUserId() != null) {
            userRepository.findById(response.getUserId()).ifPresent(u -> {
                response.setUserName(u.getFullName());
                response.setUserEmail(u.getEmail());
            });
        }
    }

    private static LocalDateTime resolveSortDate(NotificationResponse notification) {
        if (notification.getUpdatedAt() != null) {
            return notification.getUpdatedAt();
        }
        if (notification.getReviewedByHrAt() != null) {
            return notification.getReviewedByHrAt();
        }
        if (notification.getReviewedByLeaderAt() != null) {
            return notification.getReviewedByLeaderAt();
        }
        if (notification.getRequestedAt() != null) {
            return notification.getRequestedAt();
        }
        return LocalDateTime.MIN;
    }
}

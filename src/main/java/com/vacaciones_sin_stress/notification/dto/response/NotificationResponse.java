package com.vacaciones_sin_stress.notification.dto.response;

import com.vacaciones_sin_stress.common.enums.EventType;
import com.vacaciones_sin_stress.common.enums.TimeOffRequestStatus;
import com.vacaciones_sin_stress.notification.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {

    private Long timeOffRequestId;
    private NotificationType type;
    private boolean actionRequired;
    private boolean viewed;
    private Long userId;
    private String userName;
    private String userEmail;
    private TimeOffRequestStatus status;
    private EventType eventType;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer businessDays;
    private String comment;
    private String rejectionReason;
    private LocalDateTime requestedAt;
    private LocalDateTime reviewedByLeaderAt;
    private LocalDateTime reviewedByHrAt;
    private LocalDateTime updatedAt;
}

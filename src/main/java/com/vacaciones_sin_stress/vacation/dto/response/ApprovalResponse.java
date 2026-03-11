package com.vacaciones_sin_stress.vacation.dto.response;

import com.vacaciones_sin_stress.common.enums.EventType;
import com.vacaciones_sin_stress.common.enums.VacationRequestStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalResponse {

    private Long id;
    private Long userId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer requestYear;
    private Integer businessDays;
    private EventType eventType;
    private String comment;
    private VacationRequestStatus status;
    private LocalDateTime requestedAt;
    private LocalDateTime reviewedByLeaderAt;
    private LocalDateTime reviewedByHrAt;
    private Long approvedByLeaderId;
    private Long approvedByHrId;
    private boolean validatedWithClient;
    private Long validatedBy;
    private LocalDateTime validatedAt;
    private String rejectionReason;
}

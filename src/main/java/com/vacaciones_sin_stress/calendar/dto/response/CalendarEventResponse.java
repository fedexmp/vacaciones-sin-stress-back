package com.vacaciones_sin_stress.calendar.dto.response;

import com.vacaciones_sin_stress.common.enums.EventType;
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
public class CalendarEventResponse {

    private Long id;
    private Long userId;
    private String userFullName;
    private Long vacationRequestId;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private EventType eventType;
    private LocalDateTime createdAt;
}

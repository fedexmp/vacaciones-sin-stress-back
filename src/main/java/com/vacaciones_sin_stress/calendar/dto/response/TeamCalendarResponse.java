package com.vacaciones_sin_stress.calendar.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeamCalendarResponse {

    private Long currentUserId;
    private String role;
    private String scope;
    private List<CalendarEventResponse> events;
}

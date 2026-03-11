package com.vacaciones_sin_stress.dashboard.dto.response;

import com.vacaciones_sin_stress.calendar.dto.response.CalendarEventResponse;
import com.vacaciones_sin_stress.vacation.dto.response.VacationRequestResponse;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {

    private Integer currentYear;
    private Integer availableDaysCurrentYear;
    private Integer usedDaysCurrentYear;
    private List<VacationRequestResponse> recentRequests;
    private List<CalendarEventResponse> upcomingTeamAbsences;
}

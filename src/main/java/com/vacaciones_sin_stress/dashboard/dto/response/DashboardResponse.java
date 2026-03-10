package com.vacaciones_sin_stress.dashboard.dto.response;

import com.vacaciones_sin_stress.calendar.dto.response.CalendarEventResponse;
import com.vacaciones_sin_stress.vacation.dto.response.VacationRequestResponse;

import java.util.List;

public class DashboardResponse {

    private Integer currentYear;
    private Integer availableDaysCurrentYear;
    private Integer usedDaysCurrentYear;
    private List<VacationRequestResponse> recentRequests;
    private List<CalendarEventResponse> upcomingTeamAbsences;

    public DashboardResponse() {
    }

    public DashboardResponse(Integer currentYear,
                             Integer availableDaysCurrentYear,
                             Integer usedDaysCurrentYear,
                             List<VacationRequestResponse> recentRequests,
                             List<CalendarEventResponse> upcomingTeamAbsences) {
        this.currentYear = currentYear;
        this.availableDaysCurrentYear = availableDaysCurrentYear;
        this.usedDaysCurrentYear = usedDaysCurrentYear;
        this.recentRequests = recentRequests;
        this.upcomingTeamAbsences = upcomingTeamAbsences;
    }

    public Integer getCurrentYear() {
        return currentYear;
    }

    public void setCurrentYear(Integer currentYear) {
        this.currentYear = currentYear;
    }

    public Integer getAvailableDaysCurrentYear() {
        return availableDaysCurrentYear;
    }

    public void setAvailableDaysCurrentYear(Integer availableDaysCurrentYear) {
        this.availableDaysCurrentYear = availableDaysCurrentYear;
    }

    public Integer getUsedDaysCurrentYear() {
        return usedDaysCurrentYear;
    }

    public void setUsedDaysCurrentYear(Integer usedDaysCurrentYear) {
        this.usedDaysCurrentYear = usedDaysCurrentYear;
    }

    public List<VacationRequestResponse> getRecentRequests() {
        return recentRequests;
    }

    public void setRecentRequests(List<VacationRequestResponse> recentRequests) {
        this.recentRequests = recentRequests;
    }

    public List<CalendarEventResponse> getUpcomingTeamAbsences() {
        return upcomingTeamAbsences;
    }

    public void setUpcomingTeamAbsences(List<CalendarEventResponse> upcomingTeamAbsences) {
        this.upcomingTeamAbsences = upcomingTeamAbsences;
    }
}

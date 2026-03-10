package com.vacaciones_sin_stress.calendar.service;

import com.vacaciones_sin_stress.calendar.dto.response.CalendarEventResponse;
import com.vacaciones_sin_stress.calendar.dto.response.TeamCalendarResponse;

import java.util.List;

/**
 * Provides calendar views for team absences.
 */
public interface TeamCalendarService {

    /**
     * Returns current user's team calendar view.
     *
     * @return team calendar data
     */
    TeamCalendarResponse getTeamCalendar();

    /**
     * Returns upcoming team absences relevant for current user.
     *
     * @param limit max number of events
     * @return upcoming events
     */
    List<CalendarEventResponse> getUpcomingTeamAbsences(int limit);
}

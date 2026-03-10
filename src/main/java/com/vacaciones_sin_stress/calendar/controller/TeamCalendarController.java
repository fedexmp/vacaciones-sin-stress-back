package com.vacaciones_sin_stress.calendar.controller;

import com.vacaciones_sin_stress.calendar.dto.response.TeamCalendarResponse;
import com.vacaciones_sin_stress.calendar.service.TeamCalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Exposes team-calendar endpoint.
 */
@RestController
@RequiredArgsConstructor
public class TeamCalendarController {

    private final TeamCalendarService teamCalendarService;

    /**
     * Returns the calendar view relevant for current user.
     */
    @GetMapping("/team-calendar")
    public ResponseEntity<TeamCalendarResponse> getTeamCalendar() {
        return ResponseEntity.ok(teamCalendarService.getTeamCalendar());
    }
}

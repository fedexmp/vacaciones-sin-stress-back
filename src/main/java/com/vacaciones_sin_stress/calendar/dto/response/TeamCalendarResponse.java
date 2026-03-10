package com.vacaciones_sin_stress.calendar.dto.response;

import java.util.List;

public class TeamCalendarResponse {

    private Long currentUserId;
    private String role;
    private String scope;
    private List<CalendarEventResponse> events;

    public TeamCalendarResponse() {
    }

    public TeamCalendarResponse(Long currentUserId, String role, String scope, List<CalendarEventResponse> events) {
        this.currentUserId = currentUserId;
        this.role = role;
        this.scope = scope;
        this.events = events;
    }

    public Long getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(Long currentUserId) {
        this.currentUserId = currentUserId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public List<CalendarEventResponse> getEvents() {
        return events;
    }

    public void setEvents(List<CalendarEventResponse> events) {
        this.events = events;
    }
}

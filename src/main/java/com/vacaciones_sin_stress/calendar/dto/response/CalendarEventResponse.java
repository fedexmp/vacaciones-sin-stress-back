package com.vacaciones_sin_stress.calendar.dto.response;

import com.vacaciones_sin_stress.common.enums.EventType;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

    public CalendarEventResponse() {
    }

    public CalendarEventResponse(Long id,
                                 Long userId,
                                 String userFullName,
                                 Long vacationRequestId,
                                 String title,
                                 LocalDate startDate,
                                 LocalDate endDate,
                                 EventType eventType,
                                 LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.userFullName = userFullName;
        this.vacationRequestId = vacationRequestId;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.eventType = eventType;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public Long getVacationRequestId() {
        return vacationRequestId;
    }

    public void setVacationRequestId(Long vacationRequestId) {
        this.vacationRequestId = vacationRequestId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

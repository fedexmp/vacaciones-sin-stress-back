package com.vacaciones_sin_stress.vacation.dto.request;

import java.time.LocalDate;

public class CreateVacationRequestRequest {

    private LocalDate startDate;
    private LocalDate endDate;
    private String comment;

    public CreateVacationRequestRequest() {
    }

    public CreateVacationRequestRequest(LocalDate startDate, LocalDate endDate, String comment) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.comment = comment;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}

package com.vacaciones_sin_stress.vacation.dto.response;

import com.vacaciones_sin_stress.common.enums.VacationRequestStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class VacationRequestResponse {

    private Long id;
    private Long userId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer requestYear;
    private Integer businessDays;
    private String comment;
    private VacationRequestStatus status;
    private VacationRequestValidationResponse validation;
    private LocalDateTime requestedAt;
    private LocalDateTime reviewedByLeaderAt;
    private LocalDateTime reviewedByHrAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public VacationRequestResponse() {
    }

    public VacationRequestResponse(Long id,
                                   Long userId,
                                   LocalDate startDate,
                                   LocalDate endDate,
                                   Integer requestYear,
                                   Integer businessDays,
                                   String comment,
                                   VacationRequestStatus status,
                                   VacationRequestValidationResponse validation,
                                   LocalDateTime requestedAt,
                                   LocalDateTime reviewedByLeaderAt,
                                   LocalDateTime reviewedByHrAt,
                                   LocalDateTime createdAt,
                                   LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.requestYear = requestYear;
        this.businessDays = businessDays;
        this.comment = comment;
        this.status = status;
        this.validation = validation;
        this.requestedAt = requestedAt;
        this.reviewedByLeaderAt = reviewedByLeaderAt;
        this.reviewedByHrAt = reviewedByHrAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public Integer getRequestYear() {
        return requestYear;
    }

    public void setRequestYear(Integer requestYear) {
        this.requestYear = requestYear;
    }

    public Integer getBusinessDays() {
        return businessDays;
    }

    public void setBusinessDays(Integer businessDays) {
        this.businessDays = businessDays;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public VacationRequestStatus getStatus() {
        return status;
    }

    public void setStatus(VacationRequestStatus status) {
        this.status = status;
    }

    public VacationRequestValidationResponse getValidation() {
        return validation;
    }

    public void setValidation(VacationRequestValidationResponse validation) {
        this.validation = validation;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(LocalDateTime requestedAt) {
        this.requestedAt = requestedAt;
    }

    public LocalDateTime getReviewedByLeaderAt() {
        return reviewedByLeaderAt;
    }

    public void setReviewedByLeaderAt(LocalDateTime reviewedByLeaderAt) {
        this.reviewedByLeaderAt = reviewedByLeaderAt;
    }

    public LocalDateTime getReviewedByHrAt() {
        return reviewedByHrAt;
    }

    public void setReviewedByHrAt(LocalDateTime reviewedByHrAt) {
        this.reviewedByHrAt = reviewedByHrAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

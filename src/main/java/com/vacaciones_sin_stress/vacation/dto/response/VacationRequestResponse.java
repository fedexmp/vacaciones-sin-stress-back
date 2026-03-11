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
    private Long approvedByLeaderId;
    private Long approvedByHrId;
    private String rejectionReason;
    private boolean validatedWithClient;
    private Long validatedBy;
    private LocalDateTime validatedAt;
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
                                   Long approvedByLeaderId,
                                   Long approvedByHrId,
                                   String rejectionReason,
                                   boolean validatedWithClient,
                                   Long validatedBy,
                                   LocalDateTime validatedAt,
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
        this.approvedByLeaderId = approvedByLeaderId;
        this.approvedByHrId = approvedByHrId;
        this.rejectionReason = rejectionReason;
        this.validatedWithClient = validatedWithClient;
        this.validatedBy = validatedBy;
        this.validatedAt = validatedAt;
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

    public Long getApprovedByLeaderId() {
        return approvedByLeaderId;
    }

    public void setApprovedByLeaderId(Long approvedByLeaderId) {
        this.approvedByLeaderId = approvedByLeaderId;
    }

    public Long getApprovedByHrId() {
        return approvedByHrId;
    }

    public void setApprovedByHrId(Long approvedByHrId) {
        this.approvedByHrId = approvedByHrId;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public boolean isValidatedWithClient() {
        return validatedWithClient;
    }

    public void setValidatedWithClient(boolean validatedWithClient) {
        this.validatedWithClient = validatedWithClient;
    }

    public Long getValidatedBy() {
        return validatedBy;
    }

    public void setValidatedBy(Long validatedBy) {
        this.validatedBy = validatedBy;
    }

    public LocalDateTime getValidatedAt() {
        return validatedAt;
    }

    public void setValidatedAt(LocalDateTime validatedAt) {
        this.validatedAt = validatedAt;
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

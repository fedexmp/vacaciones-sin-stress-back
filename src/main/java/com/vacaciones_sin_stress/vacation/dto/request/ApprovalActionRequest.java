package com.vacaciones_sin_stress.vacation.dto.request;

public class ApprovalActionRequest {

    private String reason;
    private String rejectionReason;
    private Boolean validatedWithClient;

    public ApprovalActionRequest() {
    }

    public ApprovalActionRequest(String reason, String rejectionReason, Boolean validatedWithClient) {
        this.reason = reason;
        this.rejectionReason = rejectionReason;
        this.validatedWithClient = validatedWithClient;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public Boolean getValidatedWithClient() {
        return validatedWithClient;
    }

    public void setValidatedWithClient(Boolean validatedWithClient) {
        this.validatedWithClient = validatedWithClient;
    }
}

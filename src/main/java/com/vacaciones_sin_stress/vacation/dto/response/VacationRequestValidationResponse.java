package com.vacaciones_sin_stress.vacation.dto.response;

public class VacationRequestValidationResponse {

    private boolean warningExceededTenDays;
    private boolean warningRetroactive;

    public VacationRequestValidationResponse() {
    }

    public VacationRequestValidationResponse(boolean warningExceededTenDays, boolean warningRetroactive) {
        this.warningExceededTenDays = warningExceededTenDays;
        this.warningRetroactive = warningRetroactive;
    }

    public boolean isWarningExceededTenDays() {
        return warningExceededTenDays;
    }

    public void setWarningExceededTenDays(boolean warningExceededTenDays) {
        this.warningExceededTenDays = warningExceededTenDays;
    }

    public boolean isWarningRetroactive() {
        return warningRetroactive;
    }

    public void setWarningRetroactive(boolean warningRetroactive) {
        this.warningRetroactive = warningRetroactive;
    }
}

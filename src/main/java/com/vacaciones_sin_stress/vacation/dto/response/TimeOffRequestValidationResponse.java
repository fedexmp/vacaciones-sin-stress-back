package com.vacaciones_sin_stress.vacation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TimeOffRequestValidationResponse {

    private boolean warningExceededTenDays;
    private boolean warningRetroactive;
}


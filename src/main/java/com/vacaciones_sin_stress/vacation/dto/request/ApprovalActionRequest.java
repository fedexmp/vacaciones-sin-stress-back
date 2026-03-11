package com.vacaciones_sin_stress.vacation.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalActionRequest {

    private String reason;
    private String rejectionReason;
    private Boolean validatedWithClient;
}

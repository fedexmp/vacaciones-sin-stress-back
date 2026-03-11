package com.vacaciones_sin_stress.vacation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Required payload for reject actions.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RejectApprovalRequest {

    @Schema(description = "Reason for rejecting the time-off request",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "Se superpone con un cierre crítico del equipo")
    private String rejectionReason;
}

package com.vacaciones_sin_stress.vacation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Optional payload for final HR approval action.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HrApprovalRequest {

    @Schema(description = "Marks whether HR validated this request with client",
            example = "true")
    private Boolean validatedWithClient;
}

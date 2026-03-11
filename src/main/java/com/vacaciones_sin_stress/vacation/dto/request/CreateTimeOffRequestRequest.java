package com.vacaciones_sin_stress.vacation.dto.request;

import com.vacaciones_sin_stress.common.enums.EventType;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateTimeOffRequestRequest {

    private LocalDate startDate;
    private LocalDate endDate;
    private EventType eventType;
    private String comment;
}


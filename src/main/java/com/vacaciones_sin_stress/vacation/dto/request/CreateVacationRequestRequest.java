package com.vacaciones_sin_stress.vacation.dto.request;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateVacationRequestRequest {

    private LocalDate startDate;
    private LocalDate endDate;
    private String comment;
}

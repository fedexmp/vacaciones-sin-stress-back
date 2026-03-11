package com.vacaciones_sin_stress.balance.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrUpdateVacationBalanceRequest {

    private Long userId;
    private Integer year;
    private Integer totalDays;
    private Integer usedDays;
}

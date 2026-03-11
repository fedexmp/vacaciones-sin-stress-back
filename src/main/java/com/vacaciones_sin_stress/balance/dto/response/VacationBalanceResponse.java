package com.vacaciones_sin_stress.balance.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VacationBalanceResponse {

    private Long id;
    private Long userId;
    private Integer year;
    private Integer totalDays;
    private Integer usedDays;
    private Integer availableDays;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

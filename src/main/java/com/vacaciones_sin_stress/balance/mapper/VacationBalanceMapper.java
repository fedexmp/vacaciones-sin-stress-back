package com.vacaciones_sin_stress.balance.mapper;

import com.vacaciones_sin_stress.balance.dto.request.CreateOrUpdateVacationBalanceRequest;
import com.vacaciones_sin_stress.balance.dto.response.VacationBalanceAdminResponse;
import com.vacaciones_sin_stress.balance.dto.response.VacationBalanceResponse;
import com.vacaciones_sin_stress.balance.entity.VacationBalance;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VacationBalanceMapper {

    VacationBalanceResponse toResponse(VacationBalance vacationBalance);

    VacationBalanceAdminResponse toAdminResponse(VacationBalance vacationBalance);

    default VacationBalance toEntity(CreateOrUpdateVacationBalanceRequest request) {
        return VacationBalance.builder()
                .userId(request.getUserId())
                .year(request.getYear())
                .totalDays(request.getTotalDays())
                .usedDays(request.getUsedDays())
                .build();
    }

    default void updateEntity(CreateOrUpdateVacationBalanceRequest request, VacationBalance vacationBalance) {
        vacationBalance.setUserId(request.getUserId());
        vacationBalance.setYear(request.getYear());
        vacationBalance.setTotalDays(request.getTotalDays());
        vacationBalance.setUsedDays(request.getUsedDays());
    }
}

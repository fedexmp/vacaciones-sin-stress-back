package com.vacaciones_sin_stress.vacation.mapper;

import com.vacaciones_sin_stress.vacation.dto.response.ApprovalResponse;
import com.vacaciones_sin_stress.vacation.entity.TimeOffRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LeaderApprovalMapper {

    ApprovalResponse toApprovalResponse(TimeOffRequest timeOffRequest);
}

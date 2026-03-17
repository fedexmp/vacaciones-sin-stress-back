package com.vacaciones_sin_stress.vacation.mapper;

import com.vacaciones_sin_stress.vacation.dto.response.ApprovalResponse;
import com.vacaciones_sin_stress.vacation.entity.TimeOffRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LeaderApprovalMapper {

    @Mapping(target = "userName", ignore = true)
    @Mapping(target = "userEmail", ignore = true)
    ApprovalResponse toApprovalResponse(TimeOffRequest timeOffRequest);
}

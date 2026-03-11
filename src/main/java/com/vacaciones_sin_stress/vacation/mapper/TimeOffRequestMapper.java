package com.vacaciones_sin_stress.vacation.mapper;

import com.vacaciones_sin_stress.vacation.dto.response.TimeOffRequestResponse;
import com.vacaciones_sin_stress.vacation.dto.response.TimeOffRequestValidationResponse;
import com.vacaciones_sin_stress.vacation.entity.TimeOffRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TimeOffRequestMapper {

    @Mapping(target = "validation", expression = "java(toValidationResponse(timeOffRequest))")
    TimeOffRequestResponse toResponse(TimeOffRequest timeOffRequest);

    default TimeOffRequestValidationResponse toValidationResponse(TimeOffRequest timeOffRequest) {
        return new TimeOffRequestValidationResponse(
                timeOffRequest.isWarningExceededTenDays(),
                timeOffRequest.isWarningRetroactive()
        );
    }
}


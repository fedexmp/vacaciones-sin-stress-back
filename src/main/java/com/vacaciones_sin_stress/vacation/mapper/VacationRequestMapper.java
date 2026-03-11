package com.vacaciones_sin_stress.vacation.mapper;

import com.vacaciones_sin_stress.vacation.dto.response.VacationRequestResponse;
import com.vacaciones_sin_stress.vacation.dto.response.VacationRequestValidationResponse;
import com.vacaciones_sin_stress.vacation.entity.TimeOffRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VacationRequestMapper {

    @Mapping(target = "validation", expression = "java(toValidationResponse(vacationRequest))")
    VacationRequestResponse toResponse(TimeOffRequest vacationRequest);

    default VacationRequestValidationResponse toValidationResponse(TimeOffRequest vacationRequest) {
        return new VacationRequestValidationResponse(
                vacationRequest.isWarningExceededTenDays(),
                vacationRequest.isWarningRetroactive()
        );
    }
}

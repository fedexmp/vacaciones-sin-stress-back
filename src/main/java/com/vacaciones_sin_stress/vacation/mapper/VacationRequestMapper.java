package com.vacaciones_sin_stress.vacation.mapper;

import com.vacaciones_sin_stress.vacation.dto.response.VacationRequestResponse;
import com.vacaciones_sin_stress.vacation.dto.response.VacationRequestValidationResponse;
import com.vacaciones_sin_stress.vacation.entity.VacationRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VacationRequestMapper {

    @Mapping(target = "validation", expression = "java(toValidationResponse(vacationRequest))")
    VacationRequestResponse toResponse(VacationRequest vacationRequest);

    default VacationRequestValidationResponse toValidationResponse(VacationRequest vacationRequest) {
        return new VacationRequestValidationResponse(
                vacationRequest.isWarningExceededTenDays(),
                vacationRequest.isWarningRetroactive()
        );
    }
}

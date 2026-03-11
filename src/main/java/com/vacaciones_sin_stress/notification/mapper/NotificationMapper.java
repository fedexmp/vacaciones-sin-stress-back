package com.vacaciones_sin_stress.notification.mapper;

import com.vacaciones_sin_stress.notification.dto.response.NotificationResponse;
import com.vacaciones_sin_stress.vacation.entity.TimeOffRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    @Mapping(target = "timeOffRequestId", source = "id")
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "actionRequired", ignore = true)
    @Mapping(target = "viewed", ignore = true)
    NotificationResponse toResponse(TimeOffRequest timeOffRequest);
}

package com.vacaciones_sin_stress.calendar.mapper;

import com.vacaciones_sin_stress.calendar.dto.response.CalendarEventResponse;
import com.vacaciones_sin_stress.calendar.entity.CalendarEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CalendarEventMapper {

    @Mapping(target = "userFullName", ignore = true)
    CalendarEventResponse toResponse(CalendarEvent calendarEvent);
}

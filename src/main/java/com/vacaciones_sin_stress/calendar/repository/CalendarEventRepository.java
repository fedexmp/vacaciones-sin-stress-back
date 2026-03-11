package com.vacaciones_sin_stress.calendar.repository;

import com.vacaciones_sin_stress.calendar.entity.CalendarEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface CalendarEventRepository extends JpaRepository<CalendarEvent, Long> {

    List<CalendarEvent> findByUserId(Long userId);

    boolean existsByTimeOffRequestId(Long timeOffRequestId);

    List<CalendarEvent> findByStartDateGreaterThanEqualOrderByStartDateAsc(LocalDate startDate);

    List<CalendarEvent> findByUserIdInAndStartDateGreaterThanEqualOrderByStartDateAsc(List<Long> userIds, LocalDate startDate);
}

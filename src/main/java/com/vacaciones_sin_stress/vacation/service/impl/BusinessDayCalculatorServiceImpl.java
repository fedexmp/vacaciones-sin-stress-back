package com.vacaciones_sin_stress.vacation.service.impl;

import com.vacaciones_sin_stress.holiday.entity.Holiday;
import com.vacaciones_sin_stress.holiday.repository.HolidayRepository;
import com.vacaciones_sin_stress.vacation.service.BusinessDayCalculatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Calculates business days excluding weekends and active holidays.
 */
@Service
@RequiredArgsConstructor
public class BusinessDayCalculatorServiceImpl implements BusinessDayCalculatorService {

    private final HolidayRepository holidayRepository;

    /**
     * Excludes Saturday, Sunday and active holidays.
     */
    @Override
    public int calculateBusinessDays(LocalDate startDate, LocalDate endDate) {
        Set<LocalDate> activeHolidays = holidayRepository.findByActiveTrueAndDateBetween(startDate, endDate)
                .stream()
                .map(Holiday::getDate)
                .collect(Collectors.toSet());

        int businessDays = 0;
        LocalDate cursor = startDate;
        while (!cursor.isAfter(endDate)) {
            DayOfWeek dayOfWeek = cursor.getDayOfWeek();
            boolean isWeekend = dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
            boolean isHoliday = activeHolidays.contains(cursor);
            if (!isWeekend && !isHoliday) {
                businessDays++;
            }
            cursor = cursor.plusDays(1);
        }

        return businessDays;
    }
}

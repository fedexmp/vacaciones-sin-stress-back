package com.vacaciones_sin_stress.vacation.service;

import java.time.LocalDate;

/**
 * Calculates business days for date ranges.
 */
public interface BusinessDayCalculatorService {

    /**
     * Calculates business days between two dates, both inclusive.
     *
     * @param startDate start date
     * @param endDate end date
     * @return business days count
     */
    int calculateBusinessDays(LocalDate startDate, LocalDate endDate);
}

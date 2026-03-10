package com.vacaciones_sin_stress.holiday.repository;

import com.vacaciones_sin_stress.holiday.entity.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface HolidayRepository extends JpaRepository<Holiday, Long> {

    Optional<Holiday> findByDate(LocalDate date);

    List<Holiday> findByActiveTrueAndDateBetween(LocalDate startDate, LocalDate endDate);
}

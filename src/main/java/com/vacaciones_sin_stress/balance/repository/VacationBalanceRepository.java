package com.vacaciones_sin_stress.balance.repository;

import com.vacaciones_sin_stress.balance.entity.VacationBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface VacationBalanceRepository extends JpaRepository<VacationBalance, Long> {

    Optional<VacationBalance> findByUserIdAndYear(Long userId, Integer year);

    List<VacationBalance> findAllByOrderByUserIdAscYearDesc();

    List<VacationBalance> findByUserIdOrderByYearDesc(Long userId);

    boolean existsByUserIdAndYear(Long userId, Integer year);

    boolean existsByUserIdAndYearAndIdNot(Long userId, Integer year, Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select vb from VacationBalance vb where vb.userId = :userId and vb.year = :year")
    Optional<VacationBalance> findByUserIdAndYearForUpdate(@Param("userId") Long userId, @Param("year") Integer year);
}

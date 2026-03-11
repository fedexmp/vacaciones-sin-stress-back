package com.vacaciones_sin_stress.vacation.repository;

import com.vacaciones_sin_stress.common.enums.VacationRequestStatus;
import com.vacaciones_sin_stress.vacation.entity.TimeOffRequest;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface VacationRequestRepository extends JpaRepository<TimeOffRequest, Long>, JpaSpecificationExecutor<TimeOffRequest> {

    List<TimeOffRequest> findByUserId(Long userId);

    List<TimeOffRequest> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<TimeOffRequest> findTop5ByUserIdOrderByCreatedAtDesc(Long userId);

    List<TimeOffRequest> findByStatus(VacationRequestStatus status);

    List<TimeOffRequest> findByStatusOrderByRequestedAtAsc(VacationRequestStatus status);

    @Query("""
            select vr
            from TimeOffRequest vr
            where vr.status = :status
              and vr.userId in (
                select u.id
                from AppUser u
                where u.leaderId = :leaderId
              )
            """)
    List<TimeOffRequest> findByStatusAndLeaderId(@Param("status") VacationRequestStatus status,
                                                 @Param("leaderId") Long leaderId);

    @Query("""
            select vr
            from TimeOffRequest vr
            where vr.userId = :userId
              and vr.status = com.vacaciones_sin_stress.common.enums.VacationRequestStatus.APPROVED
              and vr.startDate <= :endDate
              and vr.endDate >= :startDate
            """)
    List<TimeOffRequest> findApprovedOverlappingRequests(@Param("userId") Long userId,
                                                         @Param("startDate") LocalDate startDate,
                                                         @Param("endDate") LocalDate endDate);

    @Query("""
            select vr
            from TimeOffRequest vr
            where vr.status = com.vacaciones_sin_stress.common.enums.VacationRequestStatus.APPROVED
              and vr.startDate <= :toDate
              and vr.endDate >= :fromDate
            order by vr.startDate asc, vr.id asc
            """)
    List<TimeOffRequest> findApprovedOverlappingRange(@Param("fromDate") LocalDate fromDate,
                                                      @Param("toDate") LocalDate toDate);

    @Query("""
            select vr
            from TimeOffRequest vr
            where vr.status = com.vacaciones_sin_stress.common.enums.VacationRequestStatus.APPROVED
              and vr.userId in :userIds
              and vr.startDate <= :toDate
              and vr.endDate >= :fromDate
            order by vr.startDate asc, vr.id asc
            """)
    List<TimeOffRequest> findApprovedByUsersOverlappingRange(@Param("userIds") List<Long> userIds,
                                                             @Param("fromDate") LocalDate fromDate,
                                                             @Param("toDate") LocalDate toDate);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select vr from TimeOffRequest vr where vr.id = :id")
    Optional<TimeOffRequest> findByIdForUpdate(@Param("id") Long id);
}

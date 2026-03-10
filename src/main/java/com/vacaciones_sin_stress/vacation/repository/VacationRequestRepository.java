package com.vacaciones_sin_stress.vacation.repository;

import com.vacaciones_sin_stress.common.enums.VacationRequestStatus;
import com.vacaciones_sin_stress.vacation.entity.VacationRequest;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface VacationRequestRepository extends JpaRepository<VacationRequest, Long> {

    List<VacationRequest> findByUserId(Long userId);

    List<VacationRequest> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<VacationRequest> findTop5ByUserIdOrderByCreatedAtDesc(Long userId);

    List<VacationRequest> findByStatus(VacationRequestStatus status);

    List<VacationRequest> findByStatusOrderByRequestedAtAsc(VacationRequestStatus status);

    @Query("""
            select vr
            from VacationRequest vr
            where vr.status = :status
              and vr.userId in (
                select u.id
                from AppUser u
                where u.leaderId = :leaderId
              )
            """)
    List<VacationRequest> findByStatusAndLeaderId(@Param("status") VacationRequestStatus status,
                                                  @Param("leaderId") Long leaderId);

    @Query("""
            select vr
            from VacationRequest vr
            where vr.userId = :userId
              and vr.status = com.vacaciones_sin_stress.common.enums.VacationRequestStatus.APPROVED
              and vr.startDate <= :endDate
              and vr.endDate >= :startDate
            """)
    List<VacationRequest> findApprovedOverlappingRequests(@Param("userId") Long userId,
                                                          @Param("startDate") LocalDate startDate,
                                                          @Param("endDate") LocalDate endDate);

    @Query("""
            select vr
            from VacationRequest vr
            where vr.status = com.vacaciones_sin_stress.common.enums.VacationRequestStatus.APPROVED
              and vr.startDate <= :toDate
              and vr.endDate >= :fromDate
            order by vr.startDate asc, vr.id asc
            """)
    List<VacationRequest> findApprovedOverlappingRange(@Param("fromDate") LocalDate fromDate,
                                                       @Param("toDate") LocalDate toDate);

    @Query("""
            select vr
            from VacationRequest vr
            where vr.status = com.vacaciones_sin_stress.common.enums.VacationRequestStatus.APPROVED
              and vr.userId in :userIds
              and vr.startDate <= :toDate
              and vr.endDate >= :fromDate
            order by vr.startDate asc, vr.id asc
            """)
    List<VacationRequest> findApprovedByUsersOverlappingRange(@Param("userIds") List<Long> userIds,
                                                              @Param("fromDate") LocalDate fromDate,
                                                              @Param("toDate") LocalDate toDate);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select vr from VacationRequest vr where vr.id = :id")
    Optional<VacationRequest> findByIdForUpdate(@Param("id") Long id);
}

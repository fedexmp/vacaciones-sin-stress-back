package com.vacaciones_sin_stress.vacation.entity;

import com.vacaciones_sin_stress.common.enums.VacationRequestStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "vacation_request",
        indexes = {
                @Index(name = "idx_vacation_request_user_id", columnList = "user_id"),
                @Index(name = "idx_vacation_request_status", columnList = "status"),
                @Index(name = "idx_vacation_request_status_user", columnList = "status,user_id"),
                @Index(name = "idx_vacation_request_user_dates", columnList = "user_id,start_date,end_date")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VacationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "request_year", nullable = false)
    private Integer requestYear;

    @Column(name = "business_days", nullable = false)
    private Integer businessDays;

    @Column(length = 500)
    private String comment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private VacationRequestStatus status;

    @Column(name = "requested_at")
    private LocalDateTime requestedAt;

    @Column(name = "reviewed_by_leader_at")
    private LocalDateTime reviewedByLeaderAt;

    @Column(name = "reviewed_by_hr_at")
    private LocalDateTime reviewedByHrAt;

    @Column(name = "approved_by_leader_id")
    private Long approvedByLeaderId;

    @Column(name = "approved_by_hr_id")
    private Long approvedByHrId;

    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;

    @Column(name = "warning_exceeded_ten_days", nullable = false)
    private boolean warningExceededTenDays;

    @Column(name = "warning_retroactive", nullable = false)
    private boolean warningRetroactive;

    @Column(name = "validated_with_client", nullable = false)
    private boolean validatedWithClient;

    @Column(name = "validated_by")
    private Long validatedBy;

    @Column(name = "validated_at")
    private LocalDateTime validatedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}

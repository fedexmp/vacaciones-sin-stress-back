package com.vacaciones_sin_stress.vacation.service.impl;

import com.vacaciones_sin_stress.auth.service.CurrentUserService;
import com.vacaciones_sin_stress.balance.entity.VacationBalance;
import com.vacaciones_sin_stress.balance.repository.VacationBalanceRepository;
import com.vacaciones_sin_stress.calendar.entity.CalendarEvent;
import com.vacaciones_sin_stress.calendar.repository.CalendarEventRepository;
import com.vacaciones_sin_stress.common.enums.EventType;
import com.vacaciones_sin_stress.common.enums.Role;
import com.vacaciones_sin_stress.common.enums.TimeOffRequestStatus;
import com.vacaciones_sin_stress.common.exception.ApiValidationException;
import com.vacaciones_sin_stress.common.exception.ConflictException;
import com.vacaciones_sin_stress.common.exception.ForbiddenOperationException;
import com.vacaciones_sin_stress.common.exception.ResourceNotFoundException;
import com.vacaciones_sin_stress.user.entity.User;
import com.vacaciones_sin_stress.vacation.dto.request.HrApprovalRequest;
import com.vacaciones_sin_stress.vacation.dto.request.RejectApprovalRequest;
import com.vacaciones_sin_stress.vacation.dto.response.ApprovalResponse;
import com.vacaciones_sin_stress.vacation.entity.TimeOffRequest;
import com.vacaciones_sin_stress.vacation.mapper.LeaderApprovalMapper;
import com.vacaciones_sin_stress.vacation.repository.TimeOffRequestRepository;
import com.vacaciones_sin_stress.vacation.service.HrApprovalService;
import com.vacaciones_sin_stress.vacation.specification.TimeOffRequestSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Final HR approval flow with transactional balance discount.
 */
@Service
@RequiredArgsConstructor
public class HrApprovalServiceImpl implements HrApprovalService {

    private final TimeOffRequestRepository timeOffRequestRepository;
    private final VacationBalanceRepository vacationBalanceRepository;
    private final CalendarEventRepository calendarEventRepository;
    private final CurrentUserService currentUserService;
    private final LeaderApprovalMapper approvalMapper;

    /**
     * Returns requests waiting for final HR decision.
     */
    @Override
    @Transactional(readOnly = true)
    public List<ApprovalResponse> getPendingHrApprovals() {
        requireHrUser();
        return timeOffRequestRepository.findByStatusOrderByRequestedAtAsc(TimeOffRequestStatus.PENDING_HR)
                .stream()
                .map(approvalMapper::toApprovalResponse)
                .toList();
    }

    /**
     * Returns HR historical view with optional filters and pagination.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ApprovalResponse> getHrHistory(TimeOffRequestStatus status,
                                               Long userId,
                                               Integer requestYear,
                                               LocalDate fromDate,
                                               LocalDate toDate,
                                               Pageable pageable) {
        requireHrUser();
        validatePeriod(fromDate, toDate);

        Specification<TimeOffRequest> specification = Specification
                .where(TimeOffRequestSpecifications.withStatus(status))
                .and(TimeOffRequestSpecifications.withUserId(userId))
                .and(TimeOffRequestSpecifications.withRequestYear(requestYear))
                .and(TimeOffRequestSpecifications.withFromDate(fromDate))
                .and(TimeOffRequestSpecifications.withToDate(toDate));

        return timeOffRequestRepository.findAll(specification, pageable)
                .map(approvalMapper::toApprovalResponse);
    }

    /**
     * Approves request, discounts annual balance and creates calendar event atomically.
     */
    @Override
    @Transactional
    public ApprovalResponse approve(Long requestId, HrApprovalRequest actionRequest) {
        User hrUser = requireHrUser();
        TimeOffRequest timeOffRequest = findPendingHrRequestForUpdate(requestId);
        if (timeOffRequest.getEventType() == EventType.VACATION) {
            discountVacationBalance(timeOffRequest, hrUser);
        }

        LocalDateTime now = LocalDateTime.now();
        timeOffRequest.setStatus(TimeOffRequestStatus.APPROVED);
        timeOffRequest.setApprovedByHrId(hrUser.getId());
        timeOffRequest.setReviewedByHrAt(now);
        timeOffRequest.setRejectionReason(null);
        applyClientValidation(timeOffRequest, actionRequest, hrUser, now);

        TimeOffRequest updatedRequest = timeOffRequestRepository.save(timeOffRequest);
        createCalendarEventIfNeeded(updatedRequest);
        return approvalMapper.toApprovalResponse(updatedRequest);
    }

    /**
     * Rejects one PENDING_HR request.
     */
    @Override
    @Transactional
    public ApprovalResponse reject(Long requestId, RejectApprovalRequest actionRequest) {
        User hrUser = requireHrUser();
        TimeOffRequest timeOffRequest = findPendingHrRequestForUpdate(requestId);

        String rejectionReason = actionRequest != null ? actionRequest.getRejectionReason() : null;
        if (!StringUtils.hasText(rejectionReason)) {
            throw new ApiValidationException("rejectionReason is required for rejection");
        }

        timeOffRequest.setStatus(TimeOffRequestStatus.REJECTED_HR);
        timeOffRequest.setReviewedByHrAt(LocalDateTime.now());
        timeOffRequest.setApprovedByHrId(hrUser.getId());
        timeOffRequest.setRejectionReason(rejectionReason.trim());

        TimeOffRequest updated = timeOffRequestRepository.save(timeOffRequest);
        return approvalMapper.toApprovalResponse(updated);
    }

    private User requireHrUser() {
        User currentUser = currentUserService.getCurrentUser();
        if (currentUser.getRole() != Role.HR) {
            throw new ForbiddenOperationException("Only HR users can access this resource");
        }
        return currentUser;
    }

    private TimeOffRequest findPendingHrRequestForUpdate(Long requestId) {
        TimeOffRequest timeOffRequest = timeOffRequestRepository.findByIdForUpdate(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Time-off request not found: " + requestId));
        if (timeOffRequest.getStatus() != TimeOffRequestStatus.PENDING_HR) {
            throw new ConflictException("Time-off request is not in PENDING_HR status");
        }
        return timeOffRequest;
    }

    private void createCalendarEventIfNeeded(TimeOffRequest timeOffRequest) {
        if (calendarEventRepository.existsByTimeOffRequestId(timeOffRequest.getId())) {
            return;
        }

        CalendarEvent event = CalendarEvent.builder()
                .userId(timeOffRequest.getUserId())
                .timeOffRequestId(timeOffRequest.getId())
                .title(resolveEventTitle(timeOffRequest.getEventType()))
                .startDate(timeOffRequest.getStartDate())
                .endDate(timeOffRequest.getEndDate())
                .eventType(timeOffRequest.getEventType())
                .build();
        calendarEventRepository.save(event);
    }

    private void discountVacationBalance(TimeOffRequest timeOffRequest, User hrUser) {
        VacationBalance vacationBalance = vacationBalanceRepository
                .findByUserIdAndYearForUpdate(timeOffRequest.getUserId(), timeOffRequest.getRequestYear())
                .orElseThrow(() -> new ConflictException(
                        "Vacation balance not found for user " + timeOffRequest.getUserId()
                                + " and year " + timeOffRequest.getRequestYear()));

        int currentUsedDays = vacationBalance.getUsedDays();
        int currentTotalDays = vacationBalance.getTotalDays();
        int currentAvailableDays = currentTotalDays - currentUsedDays;
        if (currentAvailableDays < timeOffRequest.getBusinessDays()) {
            throw new ConflictException("Insufficient available vacation days for final approval");
        }

        int updatedUsedDays = currentUsedDays + timeOffRequest.getBusinessDays();
        vacationBalance.setUsedDays(updatedUsedDays);
        vacationBalance.setAvailableDays(currentTotalDays - updatedUsedDays);
        vacationBalance.setUpdatedBy(hrUser.getId());
        vacationBalanceRepository.save(vacationBalance);
    }

    private String resolveEventTitle(EventType eventType) {
        if (eventType == EventType.LICENCIA) {
            return "Sick leave approved";
        }
        return "Vacation approved";
    }

    private void applyClientValidation(TimeOffRequest timeOffRequest,
                                       HrApprovalRequest actionRequest,
                                       User hrUser,
                                       LocalDateTime now) {
        if (actionRequest == null || actionRequest.getValidatedWithClient() == null) {
            return;
        }

        boolean validatedWithClient = actionRequest.getValidatedWithClient();
        timeOffRequest.setValidatedWithClient(validatedWithClient);
        if (validatedWithClient) {
            timeOffRequest.setValidatedBy(hrUser.getId());
            timeOffRequest.setValidatedAt(now);
            return;
        }

        timeOffRequest.setValidatedBy(null);
        timeOffRequest.setValidatedAt(null);
    }

    private void validatePeriod(LocalDate fromDate, LocalDate toDate) {
        if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
            throw new ApiValidationException("fromDate must be before or equal to toDate");
        }
    }
}


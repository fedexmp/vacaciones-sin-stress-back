package com.vacaciones_sin_stress.vacation.service.impl;

import com.vacaciones_sin_stress.auth.service.CurrentUserService;
import com.vacaciones_sin_stress.balance.entity.VacationBalance;
import com.vacaciones_sin_stress.balance.repository.VacationBalanceRepository;
import com.vacaciones_sin_stress.calendar.entity.CalendarEvent;
import com.vacaciones_sin_stress.calendar.repository.CalendarEventRepository;
import com.vacaciones_sin_stress.common.enums.EventType;
import com.vacaciones_sin_stress.common.enums.Role;
import com.vacaciones_sin_stress.common.enums.VacationRequestStatus;
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
import com.vacaciones_sin_stress.vacation.repository.VacationRequestRepository;
import com.vacaciones_sin_stress.vacation.service.HrApprovalService;
import com.vacaciones_sin_stress.vacation.specification.VacationRequestSpecifications;
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

    private final VacationRequestRepository vacationRequestRepository;
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
        return vacationRequestRepository.findByStatusOrderByRequestedAtAsc(VacationRequestStatus.PENDING_HR)
                .stream()
                .map(approvalMapper::toApprovalResponse)
                .toList();
    }

    /**
     * Returns HR historical view with optional filters and pagination.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ApprovalResponse> getHrHistory(VacationRequestStatus status,
                                               Long userId,
                                               Integer requestYear,
                                               LocalDate fromDate,
                                               LocalDate toDate,
                                               Pageable pageable) {
        requireHrUser();
        validatePeriod(fromDate, toDate);

        Specification<TimeOffRequest> specification = Specification
                .where(VacationRequestSpecifications.withStatus(status))
                .and(VacationRequestSpecifications.withUserId(userId))
                .and(VacationRequestSpecifications.withRequestYear(requestYear))
                .and(VacationRequestSpecifications.withFromDate(fromDate))
                .and(VacationRequestSpecifications.withToDate(toDate));

        return vacationRequestRepository.findAll(specification, pageable)
                .map(approvalMapper::toApprovalResponse);
    }

    /**
     * Approves request, discounts annual balance and creates calendar event atomically.
     */
    @Override
    @Transactional
    public ApprovalResponse approve(Long requestId, HrApprovalRequest actionRequest) {
        User hrUser = requireHrUser();
        TimeOffRequest vacationRequest = findPendingHrRequestForUpdate(requestId);
        if (vacationRequest.getEventType() == EventType.VACATION) {
            discountVacationBalance(vacationRequest, hrUser);
        }

        LocalDateTime now = LocalDateTime.now();
        vacationRequest.setStatus(VacationRequestStatus.APPROVED);
        vacationRequest.setApprovedByHrId(hrUser.getId());
        vacationRequest.setReviewedByHrAt(now);
        vacationRequest.setRejectionReason(null);
        applyClientValidation(vacationRequest, actionRequest, hrUser, now);

        TimeOffRequest updatedRequest = vacationRequestRepository.save(vacationRequest);
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
        TimeOffRequest vacationRequest = findPendingHrRequestForUpdate(requestId);

        String rejectionReason = actionRequest != null ? actionRequest.getRejectionReason() : null;
        if (!StringUtils.hasText(rejectionReason)) {
            throw new ApiValidationException("rejectionReason is required for rejection");
        }

        vacationRequest.setStatus(VacationRequestStatus.REJECTED_HR);
        vacationRequest.setReviewedByHrAt(LocalDateTime.now());
        vacationRequest.setApprovedByHrId(hrUser.getId());
        vacationRequest.setRejectionReason(rejectionReason.trim());

        TimeOffRequest updated = vacationRequestRepository.save(vacationRequest);
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
        TimeOffRequest vacationRequest = vacationRequestRepository.findByIdForUpdate(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Vacation request not found: " + requestId));
        if (vacationRequest.getStatus() != VacationRequestStatus.PENDING_HR) {
            throw new ConflictException("Vacation request is not in PENDING_HR status");
        }
        return vacationRequest;
    }

    private void createCalendarEventIfNeeded(TimeOffRequest vacationRequest) {
        if (calendarEventRepository.existsByVacationRequestId(vacationRequest.getId())) {
            return;
        }

        CalendarEvent event = CalendarEvent.builder()
                .userId(vacationRequest.getUserId())
                .vacationRequestId(vacationRequest.getId())
                .title(resolveEventTitle(vacationRequest.getEventType()))
                .startDate(vacationRequest.getStartDate())
                .endDate(vacationRequest.getEndDate())
                .eventType(vacationRequest.getEventType())
                .build();
        calendarEventRepository.save(event);
    }

    private void discountVacationBalance(TimeOffRequest vacationRequest, User hrUser) {
        VacationBalance vacationBalance = vacationBalanceRepository
                .findByUserIdAndYearForUpdate(vacationRequest.getUserId(), vacationRequest.getRequestYear())
                .orElseThrow(() -> new ConflictException(
                        "Vacation balance not found for user " + vacationRequest.getUserId()
                                + " and year " + vacationRequest.getRequestYear()));

        int currentUsedDays = vacationBalance.getUsedDays();
        int currentTotalDays = vacationBalance.getTotalDays();
        int currentAvailableDays = currentTotalDays - currentUsedDays;
        if (currentAvailableDays < vacationRequest.getBusinessDays()) {
            throw new ConflictException("Insufficient available vacation days for final approval");
        }

        int updatedUsedDays = currentUsedDays + vacationRequest.getBusinessDays();
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

    private void applyClientValidation(TimeOffRequest vacationRequest,
                                       HrApprovalRequest actionRequest,
                                       User hrUser,
                                       LocalDateTime now) {
        if (actionRequest == null || actionRequest.getValidatedWithClient() == null) {
            return;
        }

        boolean validatedWithClient = actionRequest.getValidatedWithClient();
        vacationRequest.setValidatedWithClient(validatedWithClient);
        if (validatedWithClient) {
            vacationRequest.setValidatedBy(hrUser.getId());
            vacationRequest.setValidatedAt(now);
            return;
        }

        vacationRequest.setValidatedBy(null);
        vacationRequest.setValidatedAt(null);
    }

    private void validatePeriod(LocalDate fromDate, LocalDate toDate) {
        if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
            throw new ApiValidationException("fromDate must be before or equal to toDate");
        }
    }
}

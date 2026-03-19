package com.vacaciones_sin_stress.vacation.service.impl;

import com.vacaciones_sin_stress.auth.service.CurrentUserService;
import com.vacaciones_sin_stress.balance.entity.VacationBalance;
import com.vacaciones_sin_stress.balance.repository.VacationBalanceRepository;
import com.vacaciones_sin_stress.common.enums.EventType;
import com.vacaciones_sin_stress.common.enums.TimeOffRequestStatus;
import com.vacaciones_sin_stress.common.exception.ApiValidationException;
import com.vacaciones_sin_stress.common.exception.ConflictException;
import com.vacaciones_sin_stress.common.exception.ForbiddenOperationException;
import com.vacaciones_sin_stress.common.exception.ResourceNotFoundException;
import com.vacaciones_sin_stress.user.entity.User;
import com.vacaciones_sin_stress.vacation.dto.request.CreateTimeOffRequestRequest;
import com.vacaciones_sin_stress.vacation.dto.response.TimeOffRequestResponse;
import com.vacaciones_sin_stress.vacation.entity.TimeOffRequest;
import com.vacaciones_sin_stress.vacation.mapper.TimeOffRequestMapper;
import com.vacaciones_sin_stress.vacation.repository.TimeOffRequestRepository;
import com.vacaciones_sin_stress.vacation.service.BusinessDayCalculatorService;
import com.vacaciones_sin_stress.vacation.service.TimeOffRequestService;
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
 * Implements MVP time-off request use cases with basic validations.
 */
@Service
@RequiredArgsConstructor
public class TimeOffRequestServiceImpl implements TimeOffRequestService {

    private final TimeOffRequestRepository timeOffRequestRepository;
    private final VacationBalanceRepository vacationBalanceRepository;
    private final BusinessDayCalculatorService businessDayCalculatorService;
    private final CurrentUserService currentUserService;
    private final TimeOffRequestMapper timeOffRequestMapper;

    /**
     * Validates and creates a time-off request in PENDING_LEADER status.
     */
    @Override
    @Transactional
    public TimeOffRequestResponse createTimeOffRequest(CreateTimeOffRequestRequest request) {
        validateRequestPayload(request);

        User currentUser = currentUserService.getCurrentUser();
        LocalDate startDate = request.getStartDate();
        LocalDate endDate = request.getEndDate();

        validateDateRange(startDate, endDate);
        int requestYear = startDate.getYear();

        int businessDays = businessDayCalculatorService.calculateBusinessDays(startDate, endDate);
        if (businessDays <= 0) {
            throw new ApiValidationException("Request must include at least one business day");
        }

        if (request.getEventType() == EventType.VACATION) {
            validateSufficientBalance(currentUser.getId(), requestYear, businessDays);
        }

        if (!timeOffRequestRepository
                .findActiveOverlappingRequests(currentUser.getId(), startDate, endDate)
                .isEmpty()) {
            throw new ConflictException("Request overlaps with an existing active request (approved or pending)");
        }

        TimeOffRequest timeOffRequest = TimeOffRequest.builder()
                .userId(currentUser.getId())
                .startDate(startDate)
                .endDate(endDate)
                .requestYear(requestYear)
                .businessDays(businessDays)
                .eventType(request.getEventType())
                .comment(trimToNull(request.getComment()))
                .status(TimeOffRequestStatus.PENDING_LEADER)
                .requestedAt(LocalDateTime.now())
                .warningExceededTenDays(businessDays > 10)
                .warningRetroactive(startDate.isBefore(LocalDate.now()))
                .validatedWithClient(false)
                .notified(false)
                .build();

        TimeOffRequest savedRequest = timeOffRequestRepository.save(timeOffRequest);
        return timeOffRequestMapper.toResponse(savedRequest);
    }

    /**
     * Returns the current user's time-off requests sorted by newest first.
     */
    @Override
    @Transactional(readOnly = true)
    public List<TimeOffRequestResponse> getMyTimeOffRequests() {
        Long userId = currentUserService.getCurrentUser().getId();
        return timeOffRequestRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(timeOffRequestMapper::toResponse)
                .toList();
    }

    /**
     * Returns current user's request history with optional filters and pagination.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<TimeOffRequestResponse> getMyTimeOffRequestsHistory(TimeOffRequestStatus status,
                                                                    Integer requestYear,
                                                                    LocalDate fromDate,
                                                                    LocalDate toDate,
                                                                    Pageable pageable) {
        validatePeriod(fromDate, toDate);

        Long userId = currentUserService.getCurrentUser().getId();
        Specification<TimeOffRequest> specification = Specification
                .where(TimeOffRequestSpecifications.forUser(userId))
                .and(TimeOffRequestSpecifications.withStatus(status))
                .and(TimeOffRequestSpecifications.withRequestYear(requestYear))
                .and(TimeOffRequestSpecifications.withFromDate(fromDate))
                .and(TimeOffRequestSpecifications.withToDate(toDate));

        return timeOffRequestRepository.findAll(specification, pageable)
                .map(timeOffRequestMapper::toResponse);
    }

    /**
     * Returns a single request only if it belongs to the current user.
     */
    @Override
    @Transactional(readOnly = true)
    public TimeOffRequestResponse getTimeOffRequestById(Long requestId) {
        User currentUser = currentUserService.getCurrentUser();
        TimeOffRequest timeOffRequest = timeOffRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Time-off request not found: " + requestId));

        if (!timeOffRequest.getUserId().equals(currentUser.getId())) {
            throw new ForbiddenOperationException("You cannot access another user's time-off request");
        }

        return timeOffRequestMapper.toResponse(timeOffRequest);
    }

    private void validateRequestPayload(CreateTimeOffRequestRequest request) {
        if (request == null) {
            throw new ApiValidationException("Request body is required");
        }
        if (request.getStartDate() == null || request.getEndDate() == null) {
            throw new ApiValidationException("startDate and endDate are required");
        }
        if (request.getEventType() == null) {
            throw new ApiValidationException("eventType is required and must be VACATION or LICENCIA");
        }
        if (request.getEventType() != EventType.VACATION && request.getEventType() != EventType.LICENCIA) {
            throw new ApiValidationException("eventType is invalid");
        }
    }

    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new ApiValidationException("startDate must be before or equal to endDate");
        }
        if (startDate.getYear() != endDate.getYear()) {
            throw new ApiValidationException("startDate and endDate must belong to the same year");
        }
    }

    private void validatePeriod(LocalDate fromDate, LocalDate toDate) {
        if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
            throw new ApiValidationException("fromDate must be before or equal to toDate");
        }
    }

    private void validateSufficientBalance(Long userId, int requestYear, int businessDays) {
        VacationBalance balance = vacationBalanceRepository.findByUserIdAndYear(userId, requestYear)
                .orElseThrow(() -> new ApiValidationException(
                        "No vacation balance found for year " + requestYear));

        if (balance.getAvailableDays() < businessDays) {
            throw new ApiValidationException(
                    "Insufficient vacation balance: " + balance.getAvailableDays()
                            + " available, " + businessDays + " requested");
        }
    }

    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }
}


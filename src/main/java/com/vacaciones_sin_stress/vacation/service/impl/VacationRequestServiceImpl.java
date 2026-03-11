package com.vacaciones_sin_stress.vacation.service.impl;

import com.vacaciones_sin_stress.auth.service.CurrentUserService;
import com.vacaciones_sin_stress.common.enums.VacationRequestStatus;
import com.vacaciones_sin_stress.common.exception.ApiValidationException;
import com.vacaciones_sin_stress.common.exception.ConflictException;
import com.vacaciones_sin_stress.common.exception.ForbiddenOperationException;
import com.vacaciones_sin_stress.common.exception.ResourceNotFoundException;
import com.vacaciones_sin_stress.user.entity.User;
import com.vacaciones_sin_stress.vacation.dto.request.CreateVacationRequestRequest;
import com.vacaciones_sin_stress.vacation.dto.response.VacationRequestResponse;
import com.vacaciones_sin_stress.vacation.entity.VacationRequest;
import com.vacaciones_sin_stress.vacation.mapper.VacationRequestMapper;
import com.vacaciones_sin_stress.vacation.repository.VacationRequestRepository;
import com.vacaciones_sin_stress.vacation.service.BusinessDayCalculatorService;
import com.vacaciones_sin_stress.vacation.service.VacationRequestService;
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
 * Implements MVP vacation-request use cases with basic validations.
 */
@Service
@RequiredArgsConstructor
public class VacationRequestServiceImpl implements VacationRequestService {

    private final VacationRequestRepository vacationRequestRepository;
    private final BusinessDayCalculatorService businessDayCalculatorService;
    private final CurrentUserService currentUserService;
    private final VacationRequestMapper vacationRequestMapper;

    /**
     * Validates and creates a vacation request in PENDING_LEADER status.
     */
    @Override
    @Transactional
    public VacationRequestResponse createVacationRequest(CreateVacationRequestRequest request) {
        validateRequestPayload(request);

        User currentUser = currentUserService.getCurrentUser();
        LocalDate startDate = request.getStartDate();
        LocalDate endDate = request.getEndDate();

        validateDateRange(startDate, endDate);
        int requestYear = startDate.getYear();

        int businessDays = businessDayCalculatorService.calculateBusinessDays(startDate, endDate);
        if (businessDays <= 0) {
            throw new ApiValidationException("Vacation request must include at least one business day");
        }

        if (!vacationRequestRepository
                .findApprovedOverlappingRequests(currentUser.getId(), startDate, endDate)
                .isEmpty()) {
            throw new ConflictException("Vacation request overlaps with an approved request");
        }

        VacationRequest vacationRequest = VacationRequest.builder()
                .userId(currentUser.getId())
                .startDate(startDate)
                .endDate(endDate)
                .requestYear(requestYear)
                .businessDays(businessDays)
                .comment(trimToNull(request.getComment()))
                .status(VacationRequestStatus.PENDING_LEADER)
                .requestedAt(LocalDateTime.now())
                .warningExceededTenDays(businessDays > 10)
                .warningRetroactive(startDate.isBefore(LocalDate.now()))
                .validatedWithClient(false)
                .build();

        VacationRequest savedRequest = vacationRequestRepository.save(vacationRequest);
        return vacationRequestMapper.toResponse(savedRequest);
    }

    /**
     * Returns the current user's vacation requests sorted by newest first.
     */
    @Override
    @Transactional(readOnly = true)
    public List<VacationRequestResponse> getMyVacationRequests() {
        Long userId = currentUserService.getCurrentUser().getId();
        return vacationRequestRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(vacationRequestMapper::toResponse)
                .toList();
    }

    /**
     * Returns current user's request history with optional filters and pagination.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<VacationRequestResponse> getMyVacationRequestsHistory(VacationRequestStatus status,
                                                                      Integer requestYear,
                                                                      LocalDate fromDate,
                                                                      LocalDate toDate,
                                                                      Pageable pageable) {
        validatePeriod(fromDate, toDate);

        Long userId = currentUserService.getCurrentUser().getId();
        Specification<VacationRequest> specification = Specification
                .where(VacationRequestSpecifications.forUser(userId))
                .and(VacationRequestSpecifications.withStatus(status))
                .and(VacationRequestSpecifications.withRequestYear(requestYear))
                .and(VacationRequestSpecifications.withFromDate(fromDate))
                .and(VacationRequestSpecifications.withToDate(toDate));

        return vacationRequestRepository.findAll(specification, pageable)
                .map(vacationRequestMapper::toResponse);
    }

    /**
     * Returns a single request only if it belongs to the current user.
     */
    @Override
    @Transactional(readOnly = true)
    public VacationRequestResponse getVacationRequestById(Long requestId) {
        User currentUser = currentUserService.getCurrentUser();
        VacationRequest vacationRequest = vacationRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Vacation request not found: " + requestId));

        if (!vacationRequest.getUserId().equals(currentUser.getId())) {
            throw new ForbiddenOperationException("You cannot access another user's vacation request");
        }

        return vacationRequestMapper.toResponse(vacationRequest);
    }

    private void validateRequestPayload(CreateVacationRequestRequest request) {
        if (request == null) {
            throw new ApiValidationException("Request body is required");
        }
        if (request.getStartDate() == null || request.getEndDate() == null) {
            throw new ApiValidationException("startDate and endDate are required");
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

    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }
}

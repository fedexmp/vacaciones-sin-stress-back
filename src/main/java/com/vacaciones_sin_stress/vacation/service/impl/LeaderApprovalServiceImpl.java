package com.vacaciones_sin_stress.vacation.service.impl;

import com.vacaciones_sin_stress.auth.service.CurrentUserService;
import com.vacaciones_sin_stress.common.enums.TimeOffRequestStatus;
import com.vacaciones_sin_stress.common.exception.ApiValidationException;
import com.vacaciones_sin_stress.common.exception.ConflictException;
import com.vacaciones_sin_stress.common.exception.ForbiddenOperationException;
import com.vacaciones_sin_stress.common.exception.ResourceNotFoundException;
import com.vacaciones_sin_stress.user.entity.User;
import com.vacaciones_sin_stress.user.repository.UserRepository;
import com.vacaciones_sin_stress.vacation.dto.request.RejectApprovalRequest;
import com.vacaciones_sin_stress.vacation.dto.response.ApprovalResponse;
import com.vacaciones_sin_stress.vacation.entity.TimeOffRequest;
import com.vacaciones_sin_stress.vacation.mapper.LeaderApprovalMapper;
import com.vacaciones_sin_stress.vacation.repository.TimeOffRequestRepository;
import com.vacaciones_sin_stress.vacation.service.LeaderApprovalService;
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
 * Executes leader approvals using only direct leaderId relationship.
 */
@Service
@RequiredArgsConstructor
public class LeaderApprovalServiceImpl implements LeaderApprovalService {

    private final TimeOffRequestRepository timeOffRequestRepository;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;
    private final LeaderApprovalMapper leaderApprovalMapper;

    /**
     * Returns only PENDING_LEADER requests for direct reports of current leader.
     */
    @Override
    @Transactional(readOnly = true)
    public List<ApprovalResponse> getPendingLeaderApprovals() {
        User leader = getCurrentUser();
        return timeOffRequestRepository
                .findByStatusAndLeaderId(TimeOffRequestStatus.PENDING_LEADER, leader.getId())
                .stream()
                .map(leaderApprovalMapper::toApprovalResponse)
                .toList();
    }

    /**
     * Returns leader's historical involvement with optional filters and pagination.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ApprovalResponse> getLeaderHistory(TimeOffRequestStatus status,
                                                   Long userId,
                                                   Integer requestYear,
                                                   LocalDate fromDate,
                                                   LocalDate toDate,
                                                   Pageable pageable) {
        User leader = getCurrentUser();
        validatePeriod(fromDate, toDate);

        Specification<TimeOffRequest> specification = Specification
                .where(TimeOffRequestSpecifications.forLeaderInvolvement(leader.getId()))
                .and(TimeOffRequestSpecifications.withStatus(status))
                .and(TimeOffRequestSpecifications.withUserId(userId))
                .and(TimeOffRequestSpecifications.withRequestYear(requestYear))
                .and(TimeOffRequestSpecifications.withFromDate(fromDate))
                .and(TimeOffRequestSpecifications.withToDate(toDate));

        return timeOffRequestRepository.findAll(specification, pageable)
                .map(leaderApprovalMapper::toApprovalResponse);
    }

    /**
     * Moves a valid request from PENDING_LEADER to PENDING_HR.
     */
    @Override
    @Transactional
    public ApprovalResponse approve(Long requestId) {
        User leader = getCurrentUser();
        TimeOffRequest timeOffRequest = findAndValidatePendingRequest(requestId, leader);

        timeOffRequest.setStatus(TimeOffRequestStatus.PENDING_HR);
        timeOffRequest.setApprovedByLeaderId(leader.getId());
        timeOffRequest.setReviewedByLeaderAt(LocalDateTime.now());
        timeOffRequest.setRejectionReason(null);

        TimeOffRequest updated = timeOffRequestRepository.save(timeOffRequest);
        return leaderApprovalMapper.toApprovalResponse(updated);
    }

    /**
     * Rejects a valid PENDING_LEADER request and stores rejection reason.
     */
    @Override
    @Transactional
    public ApprovalResponse reject(Long requestId, RejectApprovalRequest actionRequest) {
        User leader = getCurrentUser();
        TimeOffRequest timeOffRequest = findAndValidatePendingRequest(requestId, leader);

        String rejectionReason = actionRequest != null ? actionRequest.getRejectionReason() : null;
        if (!StringUtils.hasText(rejectionReason)) {
            throw new ApiValidationException("rejectionReason is required for rejection");
        }

        timeOffRequest.setStatus(TimeOffRequestStatus.REJECTED_LEADER);
        timeOffRequest.setReviewedByLeaderAt(LocalDateTime.now());
        timeOffRequest.setRejectionReason(rejectionReason.trim());
        timeOffRequest.setApprovedByLeaderId(leader.getId());

        TimeOffRequest updated = timeOffRequestRepository.save(timeOffRequest);
        return leaderApprovalMapper.toApprovalResponse(updated);
    }

    private User getCurrentUser() {
        return currentUserService.getCurrentUser();
    }

    private TimeOffRequest findAndValidatePendingRequest(Long requestId, User leader) {
        TimeOffRequest timeOffRequest = timeOffRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Time-off request not found: " + requestId));

        if (timeOffRequest.getStatus() != TimeOffRequestStatus.PENDING_LEADER) {
            throw new ConflictException("Time-off request is not in PENDING_LEADER status");
        }

        User requestOwner = userRepository.findById(timeOffRequest.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Request owner not found for time-off request: " + requestId));

        Long directLeaderId = requestOwner.getLeaderId();
        if (directLeaderId == null || !directLeaderId.equals(leader.getId())) {
            throw new ForbiddenOperationException("You can only act on direct reports' requests");
        }

        return timeOffRequest;
    }

    private void validatePeriod(LocalDate fromDate, LocalDate toDate) {
        if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
            throw new ApiValidationException("fromDate must be before or equal to toDate");
        }
    }
}


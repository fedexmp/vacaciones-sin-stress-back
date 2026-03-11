package com.vacaciones_sin_stress.vacation.service.impl;

import com.vacaciones_sin_stress.auth.service.CurrentUserService;
import com.vacaciones_sin_stress.common.enums.Role;
import com.vacaciones_sin_stress.common.enums.VacationRequestStatus;
import com.vacaciones_sin_stress.common.exception.ApiValidationException;
import com.vacaciones_sin_stress.common.exception.ConflictException;
import com.vacaciones_sin_stress.common.exception.ForbiddenOperationException;
import com.vacaciones_sin_stress.common.exception.ResourceNotFoundException;
import com.vacaciones_sin_stress.user.entity.User;
import com.vacaciones_sin_stress.user.repository.UserRepository;
import com.vacaciones_sin_stress.vacation.dto.request.ApprovalActionRequest;
import com.vacaciones_sin_stress.vacation.dto.response.ApprovalResponse;
import com.vacaciones_sin_stress.vacation.entity.VacationRequest;
import com.vacaciones_sin_stress.vacation.mapper.LeaderApprovalMapper;
import com.vacaciones_sin_stress.vacation.repository.VacationRequestRepository;
import com.vacaciones_sin_stress.vacation.service.LeaderApprovalService;
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
 * Executes leader approvals using only direct leaderId relationship.
 */
@Service
@RequiredArgsConstructor
public class LeaderApprovalServiceImpl implements LeaderApprovalService {

    private final VacationRequestRepository vacationRequestRepository;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;
    private final LeaderApprovalMapper leaderApprovalMapper;

    /**
     * Returns only PENDING_LEADER requests for direct reports of current leader.
     */
    @Override
    @Transactional(readOnly = true)
    public List<ApprovalResponse> getPendingLeaderApprovals() {
        User leader = requireLeader();
        return vacationRequestRepository
                .findByStatusAndLeaderId(VacationRequestStatus.PENDING_LEADER, leader.getId())
                .stream()
                .map(leaderApprovalMapper::toApprovalResponse)
                .toList();
    }

    /**
     * Returns leader's historical involvement with optional filters and pagination.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ApprovalResponse> getLeaderHistory(VacationRequestStatus status,
                                                   Long userId,
                                                   Integer requestYear,
                                                   LocalDate fromDate,
                                                   LocalDate toDate,
                                                   Pageable pageable) {
        User leader = requireLeader();
        validatePeriod(fromDate, toDate);

        Specification<VacationRequest> specification = Specification
                .where(VacationRequestSpecifications.forLeaderInvolvement(leader.getId()))
                .and(VacationRequestSpecifications.withStatus(status))
                .and(VacationRequestSpecifications.withUserId(userId))
                .and(VacationRequestSpecifications.withRequestYear(requestYear))
                .and(VacationRequestSpecifications.withFromDate(fromDate))
                .and(VacationRequestSpecifications.withToDate(toDate));

        return vacationRequestRepository.findAll(specification, pageable)
                .map(leaderApprovalMapper::toApprovalResponse);
    }

    /**
     * Moves a valid request from PENDING_LEADER to PENDING_HR.
     */
    @Override
    @Transactional
    public ApprovalResponse approve(Long requestId, ApprovalActionRequest actionRequest) {
        User leader = requireLeader();
        VacationRequest vacationRequest = findAndValidatePendingRequest(requestId, leader);

        vacationRequest.setStatus(VacationRequestStatus.PENDING_HR);
        vacationRequest.setApprovedByLeaderId(leader.getId());
        vacationRequest.setReviewedByLeaderAt(LocalDateTime.now());
        vacationRequest.setRejectionReason(null);

        VacationRequest updated = vacationRequestRepository.save(vacationRequest);
        return leaderApprovalMapper.toApprovalResponse(updated);
    }

    /**
     * Rejects a valid PENDING_LEADER request and stores rejection reason.
     */
    @Override
    @Transactional
    public ApprovalResponse reject(Long requestId, ApprovalActionRequest actionRequest) {
        User leader = requireLeader();
        VacationRequest vacationRequest = findAndValidatePendingRequest(requestId, leader);

        String rejectionReason = resolveRejectionReason(actionRequest);
        if (!StringUtils.hasText(rejectionReason)) {
            throw new ApiValidationException("rejectionReason is required for rejection");
        }

        vacationRequest.setStatus(VacationRequestStatus.REJECTED);
        vacationRequest.setReviewedByLeaderAt(LocalDateTime.now());
        vacationRequest.setRejectionReason(rejectionReason.trim());
        vacationRequest.setApprovedByLeaderId(leader.getId());

        VacationRequest updated = vacationRequestRepository.save(vacationRequest);
        return leaderApprovalMapper.toApprovalResponse(updated);
    }

    private User requireLeader() {
        User currentUser = currentUserService.getCurrentUser();
        if (currentUser.getRole() != Role.LEADER) {
            throw new ForbiddenOperationException("Only LEADER users can access leader approvals");
        }
        return currentUser;
    }

    private VacationRequest findAndValidatePendingRequest(Long requestId, User leader) {
        VacationRequest vacationRequest = vacationRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Vacation request not found: " + requestId));

        if (vacationRequest.getStatus() != VacationRequestStatus.PENDING_LEADER) {
            throw new ConflictException("Vacation request is not in PENDING_LEADER status");
        }

        User requestOwner = userRepository.findById(vacationRequest.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Request owner not found for vacation request: " + requestId));

        Long directLeaderId = requestOwner.getLeaderId();
        if (directLeaderId == null || !directLeaderId.equals(leader.getId())) {
            throw new ForbiddenOperationException("You can only act on direct reports' requests");
        }

        return vacationRequest;
    }

    private String resolveRejectionReason(ApprovalActionRequest actionRequest) {
        if (actionRequest == null) {
            return null;
        }
        if (StringUtils.hasText(actionRequest.getRejectionReason())) {
            return actionRequest.getRejectionReason();
        }
        return actionRequest.getReason();
    }

    private void validatePeriod(LocalDate fromDate, LocalDate toDate) {
        if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
            throw new ApiValidationException("fromDate must be before or equal to toDate");
        }
    }
}

package com.vacaciones_sin_stress.vacation.service;

import com.vacaciones_sin_stress.vacation.dto.request.ApprovalActionRequest;
import com.vacaciones_sin_stress.vacation.dto.response.ApprovalResponse;

import java.util.List;

/**
 * Handles direct-leader approval actions for vacation requests.
 */
public interface LeaderApprovalService {

    /**
     * Lists pending requests that belong to direct reports of the current leader.
     *
     * @return pending requests for direct reports
     */
    List<ApprovalResponse> getPendingLeaderApprovals();

    /**
     * Approves a pending request as direct leader.
     *
     * @param requestId request id
     * @param actionRequest optional action payload
     * @return updated request
     */
    ApprovalResponse approve(Long requestId, ApprovalActionRequest actionRequest);

    /**
     * Rejects a pending request as direct leader.
     *
     * @param requestId request id
     * @param actionRequest action payload containing rejection reason
     * @return updated request
     */
    ApprovalResponse reject(Long requestId, ApprovalActionRequest actionRequest);
}

package com.vacaciones_sin_stress.vacation.service;

import com.vacaciones_sin_stress.vacation.dto.request.ApprovalActionRequest;
import com.vacaciones_sin_stress.vacation.dto.response.ApprovalResponse;

import java.util.List;

/**
 * Handles final HR approval decisions.
 */
public interface HrApprovalService {

    /**
     * Lists all requests waiting for HR decision.
     *
     * @return requests in PENDING_HR
     */
    List<ApprovalResponse> getPendingHrApprovals();

    /**
     * Performs final approval and discounts yearly balance.
     *
     * @param requestId request id
     * @param actionRequest optional payload
     * @return updated request
     */
    ApprovalResponse approve(Long requestId, ApprovalActionRequest actionRequest);

    /**
     * Rejects one request in PENDING_HR.
     *
     * @param requestId request id
     * @param actionRequest payload containing rejection reason
     * @return updated request
     */
    ApprovalResponse reject(Long requestId, ApprovalActionRequest actionRequest);
}

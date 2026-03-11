package com.vacaciones_sin_stress.vacation.service;

import com.vacaciones_sin_stress.vacation.dto.request.RejectApprovalRequest;
import com.vacaciones_sin_stress.vacation.dto.response.ApprovalResponse;
import com.vacaciones_sin_stress.common.enums.TimeOffRequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

/**
 * Handles direct-leader approval actions for time-off requests.
 */
public interface LeaderApprovalService {

    /**
     * Lists pending requests that belong to direct reports of the current leader.
     *
     * @return pending requests for direct reports
     */
    List<ApprovalResponse> getPendingLeaderApprovals();

    /**
     * Returns leader's involvement history (pending direct reports + requests reviewed by leader).
     *
     * @param status optional status filter
     * @param userId optional user filter
     * @param requestYear optional year filter
     * @param fromDate optional start date lower bound
     * @param toDate optional end date upper bound
     * @param pageable pagination data
     * @return paged filtered history
     */
    Page<ApprovalResponse> getLeaderHistory(TimeOffRequestStatus status,
                                            Long userId,
                                            Integer requestYear,
                                            LocalDate fromDate,
                                            LocalDate toDate,
                                            Pageable pageable);

    /**
     * Approves a pending request as direct leader.
     *
     * @param requestId request id
     * @return updated request
     */
    ApprovalResponse approve(Long requestId);

    /**
     * Rejects a pending request as direct leader.
     *
     * @param requestId request id
     * @param actionRequest action payload containing rejection reason
     * @return updated request
     */
    ApprovalResponse reject(Long requestId, RejectApprovalRequest actionRequest);
}


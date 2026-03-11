package com.vacaciones_sin_stress.vacation.service;

import com.vacaciones_sin_stress.vacation.dto.request.ApprovalActionRequest;
import com.vacaciones_sin_stress.vacation.dto.response.ApprovalResponse;
import com.vacaciones_sin_stress.common.enums.VacationRequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
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
     * Returns HR history with paging and optional filters.
     *
     * @param status optional status filter
     * @param userId optional user filter
     * @param requestYear optional year filter
     * @param fromDate optional start date lower bound
     * @param toDate optional end date upper bound
     * @param pageable pagination data
     * @return paged filtered history
     */
    Page<ApprovalResponse> getHrHistory(VacationRequestStatus status,
                                        Long userId,
                                        Integer requestYear,
                                        LocalDate fromDate,
                                        LocalDate toDate,
                                        Pageable pageable);

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

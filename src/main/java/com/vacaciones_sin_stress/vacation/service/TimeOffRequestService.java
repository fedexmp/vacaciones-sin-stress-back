package com.vacaciones_sin_stress.vacation.service;

import com.vacaciones_sin_stress.vacation.dto.request.CreateTimeOffRequestRequest;
import com.vacaciones_sin_stress.vacation.dto.response.TimeOffRequestResponse;
import com.vacaciones_sin_stress.common.enums.TimeOffRequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

/**
 * Manages time-off request creation and query operations for MVP.
 */
public interface TimeOffRequestService {

    /**
     * Creates a time-off request for the current user.
     *
     * @param request create request payload
     * @return created request data
     */
    TimeOffRequestResponse createTimeOffRequest(CreateTimeOffRequestRequest request);

    /**
     * Returns all time-off requests of the current user.
     *
     * @return current user's requests
     */
    List<TimeOffRequestResponse> getMyTimeOffRequests();

    /**
     * Returns current user's requests with paging and optional filters.
     *
     * @param status optional status filter
     * @param requestYear optional year filter
     * @param fromDate optional start date lower bound
     * @param toDate optional end date upper bound
     * @param pageable pagination data
     * @return paged filtered history
     */
    Page<TimeOffRequestResponse> getMyTimeOffRequestsHistory(TimeOffRequestStatus status,
                                                             Integer requestYear,
                                                             LocalDate fromDate,
                                                             LocalDate toDate,
                                                             Pageable pageable);

    /**
     * Returns one time-off request by id if it belongs to the current user.
     *
     * @param requestId request id
     * @return request data
     */
    TimeOffRequestResponse getTimeOffRequestById(Long requestId);
}


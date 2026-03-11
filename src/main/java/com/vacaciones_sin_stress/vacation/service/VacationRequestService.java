package com.vacaciones_sin_stress.vacation.service;

import com.vacaciones_sin_stress.vacation.dto.request.CreateVacationRequestRequest;
import com.vacaciones_sin_stress.vacation.dto.response.VacationRequestResponse;
import com.vacaciones_sin_stress.common.enums.VacationRequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

/**
 * Manages vacation-request creation and query operations for MVP.
 */
public interface VacationRequestService {

    /**
     * Creates a vacation request for the current user.
     *
     * @param request create request payload
     * @return created request data
     */
    VacationRequestResponse createVacationRequest(CreateVacationRequestRequest request);

    /**
     * Returns all vacation requests of the current user.
     *
     * @return current user's requests
     */
    List<VacationRequestResponse> getMyVacationRequests();

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
    Page<VacationRequestResponse> getMyVacationRequestsHistory(VacationRequestStatus status,
                                                               Integer requestYear,
                                                               LocalDate fromDate,
                                                               LocalDate toDate,
                                                               Pageable pageable);

    /**
     * Returns one vacation request by id if it belongs to the current user.
     *
     * @param requestId request id
     * @return request data
     */
    VacationRequestResponse getVacationRequestById(Long requestId);
}

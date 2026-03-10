package com.vacaciones_sin_stress.vacation.service;

import com.vacaciones_sin_stress.vacation.dto.request.CreateVacationRequestRequest;
import com.vacaciones_sin_stress.vacation.dto.response.VacationRequestResponse;

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
     * Returns one vacation request by id if it belongs to the current user.
     *
     * @param requestId request id
     * @return request data
     */
    VacationRequestResponse getVacationRequestById(Long requestId);
}

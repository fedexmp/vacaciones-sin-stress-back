package com.vacaciones_sin_stress.vacation.controller;

import com.vacaciones_sin_stress.common.enums.VacationRequestStatus;
import com.vacaciones_sin_stress.vacation.dto.request.CreateVacationRequestRequest;
import com.vacaciones_sin_stress.vacation.dto.response.VacationRequestResponse;
import com.vacaciones_sin_stress.vacation.service.VacationRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/**
 * Exposes MVP vacation-request endpoints.
 */
@RestController
@RequestMapping("/vacation-requests")
@RequiredArgsConstructor
public class VacationRequestController {

    private final VacationRequestService vacationRequestService;

    /**
     * Creates a vacation request for the current user.
     */
    @PostMapping
    public ResponseEntity<VacationRequestResponse> createVacationRequest(
            @RequestBody CreateVacationRequestRequest request) {
        VacationRequestResponse response = vacationRequestService.createVacationRequest(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Returns vacation requests of the current user.
     */
    @GetMapping("/my")
    public ResponseEntity<List<VacationRequestResponse>> getMyVacationRequests() {
        return ResponseEntity.ok(vacationRequestService.getMyVacationRequests());
    }

    /**
     * Returns current user's request history with filters and pagination.
     */
    @GetMapping("/my/history")
    public ResponseEntity<Page<VacationRequestResponse>> getMyVacationRequestsHistory(
            @RequestParam(value = "status", required = false) VacationRequestStatus status,
            @RequestParam(value = "year", required = false) Integer year,
            @RequestParam(value = "fromDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fromDate,
            @RequestParam(value = "toDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate toDate,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(vacationRequestService.getMyVacationRequestsHistory(
                status,
                year,
                fromDate,
                toDate,
                pageRequest
        ));
    }

    /**
     * Returns one vacation request by id if it belongs to the current user.
     */
    @GetMapping("/{id}")
    public ResponseEntity<VacationRequestResponse> getVacationRequestById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(vacationRequestService.getVacationRequestById(id));
    }
}

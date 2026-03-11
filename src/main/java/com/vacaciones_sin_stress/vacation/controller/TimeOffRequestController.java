package com.vacaciones_sin_stress.vacation.controller;

import com.vacaciones_sin_stress.common.enums.TimeOffRequestStatus;
import com.vacaciones_sin_stress.vacation.dto.request.CreateTimeOffRequestRequest;
import com.vacaciones_sin_stress.vacation.dto.response.TimeOffRequestResponse;
import com.vacaciones_sin_stress.vacation.service.TimeOffRequestService;
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
 * Exposes MVP time-off request endpoints.
 */
@RestController
@RequestMapping({"/time-off-requests", "/vacation-requests"})
@RequiredArgsConstructor
public class TimeOffRequestController {

    private final TimeOffRequestService timeOffRequestService;

    /**
     * Creates a time-off request for the current user.
     */
    @PostMapping
    public ResponseEntity<TimeOffRequestResponse> createTimeOffRequest(
            @RequestBody CreateTimeOffRequestRequest request) {
        TimeOffRequestResponse response = timeOffRequestService.createTimeOffRequest(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Returns time-off requests of the current user.
     */
    @GetMapping("/my")
    public ResponseEntity<List<TimeOffRequestResponse>> getMyTimeOffRequests() {
        return ResponseEntity.ok(timeOffRequestService.getMyTimeOffRequests());
    }

    /**
     * Returns current user's request history with filters and pagination.
     */
    @GetMapping("/my/history")
    public ResponseEntity<Page<TimeOffRequestResponse>> getMyTimeOffRequestsHistory(
            @RequestParam(value = "status", required = false) TimeOffRequestStatus status,
            @RequestParam(value = "year", required = false) Integer year,
            @RequestParam(value = "fromDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fromDate,
            @RequestParam(value = "toDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate toDate,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(timeOffRequestService.getMyTimeOffRequestsHistory(
                status,
                year,
                fromDate,
                toDate,
                pageRequest
        ));
    }

    /**
     * Returns one time-off request by id if it belongs to the current user.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TimeOffRequestResponse> getTimeOffRequestById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(timeOffRequestService.getTimeOffRequestById(id));
    }
}


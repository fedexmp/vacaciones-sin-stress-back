package com.vacaciones_sin_stress.vacation.controller;

import com.vacaciones_sin_stress.common.enums.TimeOffRequestStatus;
import com.vacaciones_sin_stress.vacation.dto.request.HrApprovalRequest;
import com.vacaciones_sin_stress.vacation.dto.request.RejectApprovalRequest;
import com.vacaciones_sin_stress.vacation.dto.response.ApprovalResponse;
import com.vacaciones_sin_stress.vacation.service.HrApprovalService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
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
 * Exposes final HR approval endpoints.
 */
@RestController
@RequestMapping("/approvals/hr")
@RequiredArgsConstructor
public class HrApprovalController {

    private final HrApprovalService hrApprovalService;

    /**
     * Lists requests pending final HR review.
     */
    @GetMapping
    public ResponseEntity<List<ApprovalResponse>> getPendingApprovals() {
        return ResponseEntity.ok(hrApprovalService.getPendingHrApprovals());
    }

    /**
     * Returns HR history with filters and pagination.
     */
    @GetMapping("/history")
    public ResponseEntity<Page<ApprovalResponse>> getHrHistory(
            @RequestParam(value = "status", required = false) TimeOffRequestStatus status,
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "year", required = false) Integer year,
            @RequestParam(value = "fromDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fromDate,
            @RequestParam(value = "toDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate toDate,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(hrApprovalService.getHrHistory(
                status,
                userId,
                year,
                fromDate,
                toDate,
                pageRequest
        ));
    }

    /**
     * Approves one request in PENDING_HR.
     */
    @PostMapping("/{id}/approve")
    public ResponseEntity<ApprovalResponse> approve(@PathVariable("id") Long id,
                                                    @RequestBody(required = false) HrApprovalRequest request) {
        return ResponseEntity.ok(hrApprovalService.approve(id, request));
    }

    /**
     * Rejects one request in PENDING_HR.
     */
    @PostMapping("/{id}/reject")
    public ResponseEntity<ApprovalResponse> reject(@PathVariable("id") Long id,
                                                   @RequestBody RejectApprovalRequest request) {
        return ResponseEntity.ok(hrApprovalService.reject(id, request));
    }
}


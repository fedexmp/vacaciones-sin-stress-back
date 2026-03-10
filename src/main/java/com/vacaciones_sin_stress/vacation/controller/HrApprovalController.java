package com.vacaciones_sin_stress.vacation.controller;

import com.vacaciones_sin_stress.vacation.dto.request.ApprovalActionRequest;
import com.vacaciones_sin_stress.vacation.dto.response.ApprovalResponse;
import com.vacaciones_sin_stress.vacation.service.HrApprovalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
     * Approves one request in PENDING_HR.
     */
    @PostMapping("/{id}/approve")
    public ResponseEntity<ApprovalResponse> approve(@PathVariable("id") Long id,
                                                    @RequestBody(required = false) ApprovalActionRequest request) {
        return ResponseEntity.ok(hrApprovalService.approve(id, request));
    }

    /**
     * Rejects one request in PENDING_HR.
     */
    @PostMapping("/{id}/reject")
    public ResponseEntity<ApprovalResponse> reject(@PathVariable("id") Long id,
                                                   @RequestBody(required = false) ApprovalActionRequest request) {
        return ResponseEntity.ok(hrApprovalService.reject(id, request));
    }
}

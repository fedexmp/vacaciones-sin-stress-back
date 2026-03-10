package com.vacaciones_sin_stress.vacation.controller;

import com.vacaciones_sin_stress.vacation.dto.request.ApprovalActionRequest;
import com.vacaciones_sin_stress.vacation.dto.response.ApprovalResponse;
import com.vacaciones_sin_stress.vacation.service.LeaderApprovalService;
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
 * Exposes direct-leader approval endpoints.
 */
@RestController
@RequestMapping("/approvals/leader")
@RequiredArgsConstructor
public class LeaderApprovalController {

    private final LeaderApprovalService leaderApprovalService;

    /**
     * Lists pending requests for direct reports of current leader.
     */
    @GetMapping
    public ResponseEntity<List<ApprovalResponse>> getPendingApprovals() {
        return ResponseEntity.ok(leaderApprovalService.getPendingLeaderApprovals());
    }

    /**
     * Approves a pending request as direct leader.
     */
    @PostMapping("/{id}/approve")
    public ResponseEntity<ApprovalResponse> approve(@PathVariable("id") Long id,
                                                    @RequestBody(required = false) ApprovalActionRequest request) {
        return ResponseEntity.ok(leaderApprovalService.approve(id, request));
    }

    /**
     * Rejects a pending request as direct leader.
     */
    @PostMapping("/{id}/reject")
    public ResponseEntity<ApprovalResponse> reject(@PathVariable("id") Long id,
                                                   @RequestBody(required = false) ApprovalActionRequest request) {
        return ResponseEntity.ok(leaderApprovalService.reject(id, request));
    }
}

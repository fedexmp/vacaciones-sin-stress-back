package com.vacaciones_sin_stress.balance.controller;

import com.vacaciones_sin_stress.balance.dto.request.CreateOrUpdateVacationBalanceRequest;
import com.vacaciones_sin_stress.balance.dto.response.VacationBalanceAdminResponse;
import com.vacaciones_sin_stress.balance.service.VacationBalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Exposes HR endpoints for annual vacation balances.
 */
@RestController
@RequestMapping("/hr")
@RequiredArgsConstructor
public class HrVacationBalanceController {

    private final VacationBalanceService vacationBalanceService;

    /**
     * Creates a yearly balance for one user.
     */
    @PostMapping("/balances")
    public ResponseEntity<VacationBalanceAdminResponse> createBalance(
            @RequestBody CreateOrUpdateVacationBalanceRequest request) {
        VacationBalanceAdminResponse response = vacationBalanceService.createBalance(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Updates one yearly balance.
     */
    @PutMapping("/balances/{id}")
    public ResponseEntity<VacationBalanceAdminResponse> updateBalance(
            @PathVariable("id") Long id,
            @RequestBody CreateOrUpdateVacationBalanceRequest request) {
        return ResponseEntity.ok(vacationBalanceService.updateBalance(id, request));
    }

    /**
     * Deletes one balance by id.
     */
    @DeleteMapping("/balances/{id}")
    public ResponseEntity<Void> deleteBalance(@PathVariable("id") Long id) {
        vacationBalanceService.deleteBalance(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Lists all yearly balances.
     */
    @GetMapping("/balances")
    public ResponseEntity<List<VacationBalanceAdminResponse>> getAllBalances() {
        return ResponseEntity.ok(vacationBalanceService.getAllBalances());
    }

    /**
     * Returns one balance by user/year.
     */
    @GetMapping("/balances/{userId}/{year}")
    public ResponseEntity<VacationBalanceAdminResponse> getBalanceByUserAndYear(
            @PathVariable("userId") Long userId,
            @PathVariable("year") Integer year) {
        return ResponseEntity.ok(vacationBalanceService.getBalanceByUserAndYear(userId, year));
    }

    /**
     * Lists balances of one user.
     */
    @GetMapping("/users/{userId}/balances")
    public ResponseEntity<List<VacationBalanceAdminResponse>> getBalancesByUser(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(vacationBalanceService.getBalancesByUser(userId));
    }
}

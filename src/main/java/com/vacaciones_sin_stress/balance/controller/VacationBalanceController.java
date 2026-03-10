package com.vacaciones_sin_stress.balance.controller;

import com.vacaciones_sin_stress.balance.dto.response.VacationBalanceResponse;
import com.vacaciones_sin_stress.balance.service.VacationBalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Exposes self-service balance endpoints for current user.
 */
@RestController
@RequestMapping("/balances")
@RequiredArgsConstructor
public class VacationBalanceController {

    private final VacationBalanceService vacationBalanceService;

    /**
     * Returns balances of current user.
     */
    @GetMapping("/me")
    public ResponseEntity<List<VacationBalanceResponse>> getMyBalances() {
        return ResponseEntity.ok(vacationBalanceService.getMyBalances());
    }

    /**
     * Returns one balance of current user by year.
     */
    @GetMapping("/me/{year}")
    public ResponseEntity<VacationBalanceResponse> getMyBalanceByYear(@PathVariable("year") Integer year) {
        return ResponseEntity.ok(vacationBalanceService.getMyBalanceByYear(year));
    }
}

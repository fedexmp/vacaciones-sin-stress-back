package com.vacaciones_sin_stress.balance.service;

import com.vacaciones_sin_stress.balance.dto.request.CreateOrUpdateVacationBalanceRequest;
import com.vacaciones_sin_stress.balance.dto.response.VacationBalanceAdminResponse;
import com.vacaciones_sin_stress.balance.dto.response.VacationBalanceResponse;

import java.util.List;

/**
 * Manages annual vacation balances for HR and self-service queries.
 */
public interface VacationBalanceService {

    /**
     * Creates a yearly balance for one user.
     *
     * @param request balance data
     * @return created balance
     */
    VacationBalanceAdminResponse createBalance(CreateOrUpdateVacationBalanceRequest request);

    /**
     * Updates one existing yearly balance.
     *
     * @param id balance id
     * @param request updated data
     * @return updated balance
     */
    VacationBalanceAdminResponse updateBalance(Long id, CreateOrUpdateVacationBalanceRequest request);

    /**
     * Lists all balances for HR.
     *
     * @return all balances
     */
    List<VacationBalanceAdminResponse> getAllBalances();

    /**
     * Returns one user/year balance for HR.
     *
     * @param userId user id
     * @param year year
     * @return requested balance
     */
    VacationBalanceAdminResponse getBalanceByUserAndYear(Long userId, Integer year);

    /**
     * Lists all balances for one user for HR.
     *
     * @param userId user id
     * @return user balances
     */
    List<VacationBalanceAdminResponse> getBalancesByUser(Long userId);

    /**
     * Deletes one balance by id.
     *
     * @param id balance id
     */
    void deleteBalance(Long id);

    /**
     * Returns current user's balances.
     *
     * @return current user's balances
     */
    List<VacationBalanceResponse> getMyBalances();

    /**
     * Returns current user's balance for one year.
     *
     * @param year year
     * @return balance
     */
    VacationBalanceResponse getMyBalanceByYear(Integer year);
}

package com.vacaciones_sin_stress.balance.service.impl;

import com.vacaciones_sin_stress.auth.service.CurrentUserService;
import com.vacaciones_sin_stress.balance.dto.request.CreateOrUpdateVacationBalanceRequest;
import com.vacaciones_sin_stress.balance.dto.response.VacationBalanceAdminResponse;
import com.vacaciones_sin_stress.balance.dto.response.VacationBalanceResponse;
import com.vacaciones_sin_stress.balance.entity.VacationBalance;
import com.vacaciones_sin_stress.balance.mapper.VacationBalanceMapper;
import com.vacaciones_sin_stress.balance.repository.VacationBalanceRepository;
import com.vacaciones_sin_stress.balance.service.VacationBalanceService;
import com.vacaciones_sin_stress.common.enums.Role;
import com.vacaciones_sin_stress.common.exception.ApiValidationException;
import com.vacaciones_sin_stress.common.exception.ConflictException;
import com.vacaciones_sin_stress.common.exception.ForbiddenOperationException;
import com.vacaciones_sin_stress.common.exception.ResourceNotFoundException;
import com.vacaciones_sin_stress.user.entity.User;
import com.vacaciones_sin_stress.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Provides simple HR operations for annual vacation balances.
 */
@Service
@RequiredArgsConstructor
public class VacationBalanceServiceImpl implements VacationBalanceService {

    private final VacationBalanceRepository vacationBalanceRepository;
    private final VacationBalanceMapper vacationBalanceMapper;
    private final CurrentUserService currentUserService;
    private final UserRepository userRepository;

    /**
     * Creates one balance and stores HR as creator/updater.
     */
    @Override
    @Transactional
    public VacationBalanceAdminResponse createBalance(CreateOrUpdateVacationBalanceRequest request) {
        User hrUser = requireHrUser();
        validateRequest(request);
        requireUserExists(request.getUserId());

        if (vacationBalanceRepository.existsByUserIdAndYear(request.getUserId(), request.getYear())) {
            throw new ConflictException("Vacation balance already exists for user/year");
        }

        VacationBalance vacationBalance = vacationBalanceMapper.toEntity(request);
        vacationBalance.setAvailableDays(calculateAvailableDays(request.getTotalDays(), request.getUsedDays()));
        vacationBalance.setCreatedBy(hrUser.getId());
        vacationBalance.setUpdatedBy(hrUser.getId());

        VacationBalance saved = vacationBalanceRepository.save(vacationBalance);
        return enrich(vacationBalanceMapper.toAdminResponse(saved));
    }

    /**
     * Updates one balance and keeps availability consistent.
     */
    @Override
    @Transactional
    public VacationBalanceAdminResponse updateBalance(Long id, CreateOrUpdateVacationBalanceRequest request) {
        User hrUser = requireHrUser();
        validateRequest(request);
        requireUserExists(request.getUserId());

        VacationBalance existingBalance = vacationBalanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vacation balance not found: " + id));

        if (vacationBalanceRepository.existsByUserIdAndYearAndIdNot(request.getUserId(), request.getYear(), id)) {
            throw new ConflictException("Vacation balance already exists for user/year");
        }

        vacationBalanceMapper.updateEntity(request, existingBalance);
        existingBalance.setAvailableDays(calculateAvailableDays(request.getTotalDays(), request.getUsedDays()));
        existingBalance.setUpdatedBy(hrUser.getId());

        VacationBalance saved = vacationBalanceRepository.save(existingBalance);
        return enrich(vacationBalanceMapper.toAdminResponse(saved));
    }

    /**
     * Lists all balances for HR.
     */
    @Override
    @Transactional(readOnly = true)
    public List<VacationBalanceAdminResponse> getAllBalances() {
        requireHrUser();
        return vacationBalanceRepository.findAllByOrderByUserIdAscYearDesc()
                .stream()
                .map(vacationBalanceMapper::toAdminResponse)
                .map(this::enrich)
                .toList();
    }

    /**
     * Returns one user/year balance for HR.
     */
    @Override
    @Transactional(readOnly = true)
    public VacationBalanceAdminResponse getBalanceByUserAndYear(Long userId, Integer year) {
        requireHrUser();
        VacationBalance balance = vacationBalanceRepository.findByUserIdAndYear(userId, year)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Vacation balance not found for user " + userId + " and year " + year));
        return enrich(vacationBalanceMapper.toAdminResponse(balance));
    }

    /**
     * Deletes one balance by id.
     */
    @Override
    @Transactional
    public void deleteBalance(Long id) {
        requireHrUser();
        VacationBalance balance = vacationBalanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vacation balance not found: " + id));
        vacationBalanceRepository.delete(balance);
    }

    /**
     * Returns all balances of one user for HR.
     */
    @Override
    @Transactional(readOnly = true)
    public List<VacationBalanceAdminResponse> getBalancesByUser(Long userId) {
        requireHrUser();
        requireUserExists(userId);
        return vacationBalanceRepository.findByUserIdOrderByYearDesc(userId)
                .stream()
                .map(vacationBalanceMapper::toAdminResponse)
                .map(this::enrich)
                .toList();
    }

    /**
     * Lists balances of current user.
     */
    @Override
    @Transactional(readOnly = true)
    public List<VacationBalanceResponse> getMyBalances() {
        Long userId = currentUserService.getCurrentUser().getId();
        return vacationBalanceRepository.findByUserIdOrderByYearDesc(userId)
                .stream()
                .map(vacationBalanceMapper::toResponse)
                .toList();
    }

    /**
     * Returns current user's one-year balance.
     */
    @Override
    @Transactional(readOnly = true)
    public VacationBalanceResponse getMyBalanceByYear(Integer year) {
        Long userId = currentUserService.getCurrentUser().getId();
        VacationBalance balance = vacationBalanceRepository.findByUserIdAndYear(userId, year)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Vacation balance not found for year " + year));
        return vacationBalanceMapper.toResponse(balance);
    }

    private VacationBalanceAdminResponse enrich(VacationBalanceAdminResponse r) {
        userRepository.findById(r.getUserId()).ifPresent(u -> {
            r.setUserName(u.getFullName());
            r.setUserEmail(u.getEmail());
        });
        if (r.getCreatedBy() != null) {
            userRepository.findById(r.getCreatedBy())
                    .ifPresent(u -> r.setCreatedByName(u.getFullName()));
        }
        if (r.getUpdatedBy() != null) {
            userRepository.findById(r.getUpdatedBy())
                    .ifPresent(u -> r.setUpdatedByName(u.getFullName()));
        }
        return r;
    }

    private User requireHrUser() {
        User currentUser = currentUserService.getCurrentUser();
        if (currentUser.getRole() != Role.HR) {
            throw new ForbiddenOperationException("Only HR users can access this resource");
        }
        return currentUser;
    }

    private void validateRequest(CreateOrUpdateVacationBalanceRequest request) {
        if (request == null) {
            throw new ApiValidationException("Request body is required");
        }
        if (request.getUserId() == null || request.getYear() == null
                || request.getTotalDays() == null || request.getUsedDays() == null) {
            throw new ApiValidationException("userId, year, totalDays and usedDays are required");
        }
        if (request.getTotalDays() < 0 || request.getUsedDays() < 0) {
            throw new ApiValidationException("totalDays and usedDays must be non-negative");
        }
        if (request.getUsedDays() > request.getTotalDays()) {
            throw new ApiValidationException("usedDays cannot be greater than totalDays");
        }
    }

    private int calculateAvailableDays(Integer totalDays, Integer usedDays) {
        int availableDays = totalDays - usedDays;
        if (availableDays < 0) {
            throw new ApiValidationException("availableDays cannot be negative");
        }
        return availableDays;
    }

    private void requireUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found: " + userId);
        }
    }
}

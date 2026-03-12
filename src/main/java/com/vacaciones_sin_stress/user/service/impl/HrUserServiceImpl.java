package com.vacaciones_sin_stress.user.service.impl;

import com.vacaciones_sin_stress.auth.service.CurrentUserService;
import com.vacaciones_sin_stress.common.enums.Role;
import com.vacaciones_sin_stress.common.exception.ForbiddenOperationException;
import com.vacaciones_sin_stress.user.dto.response.UserSummaryResponse;
import com.vacaciones_sin_stress.user.entity.User;
import com.vacaciones_sin_stress.user.repository.UserRepository;
import com.vacaciones_sin_stress.user.service.HrUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HrUserServiceImpl implements HrUserService {

    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;

    @Override
    @Transactional(readOnly = true)
    public List<UserSummaryResponse> getAllActiveUsers() {
        requireHrUser();

        return userRepository.findAll().stream()
                .filter(User::isActive)
                .sorted(Comparator.comparing(User::getFullName, String.CASE_INSENSITIVE_ORDER))
                .map(u -> UserSummaryResponse.builder()
                        .id(u.getId())
                        .name(u.getFullName())
                        .email(u.getEmail())
                        .build())
                .toList();
    }

    private void requireHrUser() {
        User currentUser = currentUserService.getCurrentUser();
        if (currentUser.getRole() != Role.HR) {
            throw new ForbiddenOperationException("Only HR users can access this resource");
        }
    }
}

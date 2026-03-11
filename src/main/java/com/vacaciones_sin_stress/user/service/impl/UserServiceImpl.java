package com.vacaciones_sin_stress.user.service.impl;

import com.vacaciones_sin_stress.auth.service.CurrentUserService;
import com.vacaciones_sin_stress.user.dto.response.UserMeResponse;
import com.vacaciones_sin_stress.user.mapper.UserMapper;
import com.vacaciones_sin_stress.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Resolves authenticated user profile for /me endpoint.
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final CurrentUserService currentUserService;
    private final UserMapper userMapper;

    /**
     * Returns authenticated user data.
     */
    @Override
    @Transactional(readOnly = true)
    public UserMeResponse getMe() {
        return userMapper.toMeResponse(currentUserService.getCurrentUser());
    }
}

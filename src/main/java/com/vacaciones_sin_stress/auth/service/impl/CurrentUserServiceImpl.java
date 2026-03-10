package com.vacaciones_sin_stress.auth.service.impl;

import com.vacaciones_sin_stress.auth.context.AuthContextHolder;
import com.vacaciones_sin_stress.auth.model.AuthenticatedPrincipal;
import com.vacaciones_sin_stress.auth.service.CurrentUserService;
import com.vacaciones_sin_stress.common.exception.ForbiddenOperationException;
import com.vacaciones_sin_stress.common.exception.UnauthorizedException;
import com.vacaciones_sin_stress.user.entity.User;
import com.vacaciones_sin_stress.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

/**
 * Resolves current internal user from request authentication context.
 */
@Service
@RequiredArgsConstructor
public class CurrentUserServiceImpl implements CurrentUserService {

    private final UserRepository userRepository;

    /**
     * Resolves the current user by Clerk identity first and falls back to email mapping.
     */
    @Override
    public User getCurrentUser() {
        AuthenticatedPrincipal principal = AuthContextHolder.getPrincipal()
                .orElseThrow(() -> new UnauthorizedException("Missing authenticated principal"));

        String clerkUserId = trimToNull(principal.getClerkUserId());
        String email = trimToNull(principal.getEmail());
        if (!StringUtils.hasText(clerkUserId) && !StringUtils.hasText(email)) {
            throw new UnauthorizedException("Authenticated principal does not contain clerkUserId or email");
        }

        Optional<User> byClerkId = StringUtils.hasText(clerkUserId)
                ? userRepository.findByClerkUserId(clerkUserId)
                : Optional.empty();
        Optional<User> byEmail = !byClerkId.isPresent() && StringUtils.hasText(email)
                ? userRepository.findByEmail(email)
                : Optional.empty();

        User user = byClerkId.or(() -> byEmail)
                .orElseThrow(() -> new UnauthorizedException(
                        "Authenticated user is not mapped in internal users table"));

        if (!user.isActive()) {
            throw new ForbiddenOperationException("Authenticated user is inactive");
        }

        if (StringUtils.hasText(clerkUserId) && StringUtils.hasText(user.getClerkUserId())
                && !clerkUserId.equals(user.getClerkUserId())) {
            throw new UnauthorizedException("Clerk user id does not match internal user mapping");
        }

        if (StringUtils.hasText(clerkUserId) && !StringUtils.hasText(user.getClerkUserId())) {
            user.setClerkUserId(clerkUserId);
            user = userRepository.save(user);
        }

        return user;
    }

    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }
}

package com.vacaciones_sin_stress.auth.config;

import com.vacaciones_sin_stress.auth.context.AuthContextHolder;
import com.vacaciones_sin_stress.auth.model.AuthenticatedPrincipal;
import com.vacaciones_sin_stress.auth.service.AuthenticatedPrincipalResolver;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Populates request authentication context using pluggable principal resolvers.
 */
@Component
@RequiredArgsConstructor
public class AuthenticationContextFilter extends OncePerRequestFilter {

    private final List<AuthenticatedPrincipalResolver> principalResolvers;

    /**
     * Extracts principal data once per request and clears context at the end.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        Optional<AuthenticatedPrincipal> principal = principalResolvers.stream()
                .map(resolver -> resolver.resolve(request))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();

        principal.ifPresent(AuthContextHolder::setPrincipal);

        try {
            filterChain.doFilter(request, response);
        } finally {
            AuthContextHolder.clear();
        }
    }
}

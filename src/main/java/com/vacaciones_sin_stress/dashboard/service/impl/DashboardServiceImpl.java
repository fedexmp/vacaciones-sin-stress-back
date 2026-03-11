package com.vacaciones_sin_stress.dashboard.service.impl;

import com.vacaciones_sin_stress.auth.service.CurrentUserService;
import com.vacaciones_sin_stress.balance.entity.VacationBalance;
import com.vacaciones_sin_stress.balance.repository.VacationBalanceRepository;
import com.vacaciones_sin_stress.calendar.dto.response.CalendarEventResponse;
import com.vacaciones_sin_stress.calendar.service.TeamCalendarService;
import com.vacaciones_sin_stress.dashboard.dto.response.DashboardResponse;
import com.vacaciones_sin_stress.dashboard.service.DashboardService;
import com.vacaciones_sin_stress.user.entity.User;
import com.vacaciones_sin_stress.vacation.dto.response.TimeOffRequestResponse;
import com.vacaciones_sin_stress.vacation.mapper.TimeOffRequestMapper;
import com.vacaciones_sin_stress.vacation.repository.TimeOffRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Aggregates dashboard data from existing services/repositories.
 */
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private static final int RECENT_REQUESTS_LIMIT = 5;
    private static final int UPCOMING_TEAM_ABSENCES_LIMIT = 10;

    private final CurrentUserService currentUserService;
    private final VacationBalanceRepository vacationBalanceRepository;
    private final TimeOffRequestRepository timeOffRequestRepository;
    private final TimeOffRequestMapper timeOffRequestMapper;
    private final TeamCalendarService teamCalendarService;

    /**
     * Returns current year balance summary, recent requests and upcoming team absences.
     */
    @Override
    @Transactional(readOnly = true)
    public DashboardResponse getMyDashboard() {
        User currentUser = currentUserService.getCurrentUser();
        int currentYear = LocalDate.now().getYear();

        Optional<VacationBalance> optionalBalance = vacationBalanceRepository
                .findByUserIdAndYear(currentUser.getId(), currentYear);
        Integer availableDays = optionalBalance.map(VacationBalance::getAvailableDays).orElse(null);
        Integer usedDays = optionalBalance.map(VacationBalance::getUsedDays).orElse(null);

        List<TimeOffRequestResponse> recentRequests = timeOffRequestRepository
                .findTop5ByUserIdOrderByCreatedAtDesc(currentUser.getId())
                .stream()
                .limit(RECENT_REQUESTS_LIMIT)
                .map(timeOffRequestMapper::toResponse)
                .toList();

        List<CalendarEventResponse> upcomingTeamAbsences = teamCalendarService
                .getUpcomingTeamAbsences(UPCOMING_TEAM_ABSENCES_LIMIT);

        return new DashboardResponse(
                currentYear,
                availableDays,
                usedDays,
                recentRequests,
                upcomingTeamAbsences
        );
    }
}


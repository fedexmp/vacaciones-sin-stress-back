package com.vacaciones_sin_stress.calendar.service.impl;

import com.vacaciones_sin_stress.auth.service.CurrentUserService;
import com.vacaciones_sin_stress.calendar.dto.response.CalendarEventResponse;
import com.vacaciones_sin_stress.calendar.dto.response.TeamCalendarResponse;
import com.vacaciones_sin_stress.calendar.entity.CalendarEvent;
import com.vacaciones_sin_stress.calendar.mapper.CalendarEventMapper;
import com.vacaciones_sin_stress.calendar.repository.CalendarEventRepository;
import com.vacaciones_sin_stress.calendar.service.TeamCalendarService;
import com.vacaciones_sin_stress.common.enums.Role;
import com.vacaciones_sin_stress.common.exception.ApiValidationException;
import com.vacaciones_sin_stress.user.entity.User;
import com.vacaciones_sin_stress.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Returns team-calendar events using a simple role-based scope.
 */
@Service
@RequiredArgsConstructor
public class TeamCalendarServiceImpl implements TeamCalendarService {

    private final CalendarEventRepository calendarEventRepository;
    private final CalendarEventMapper calendarEventMapper;
    private final CurrentUserService currentUserService;
    private final UserRepository userRepository;

    /**
     * Builds the full team-calendar response for current user.
     */
    @Override
    @Transactional(readOnly = true)
    public TeamCalendarResponse getTeamCalendar() {
        User currentUser = currentUserService.getCurrentUser();
        List<CalendarEventResponse> events = resolveUpcomingEventsForCurrentUser(currentUser);
        String scope = currentUser.getRole() == Role.HR ? "GLOBAL" : "TEAM";
        return new TeamCalendarResponse(
                currentUser.getId(),
                currentUser.getRole().name(),
                scope,
                events
        );
    }

    /**
     * Returns at most limit upcoming team absences for current user.
     */
    @Override
    @Transactional(readOnly = true)
    public List<CalendarEventResponse> getUpcomingTeamAbsences(int limit) {
        if (limit <= 0) {
            throw new ApiValidationException("limit must be greater than zero");
        }
        User currentUser = currentUserService.getCurrentUser();
        List<CalendarEventResponse> events = resolveUpcomingEventsForCurrentUser(currentUser);
        if (events.size() <= limit) {
            return events;
        }
        return new ArrayList<>(events.subList(0, limit));
    }

    private List<CalendarEventResponse> resolveUpcomingEventsForCurrentUser(User currentUser) {
        LocalDate today = LocalDate.now();
        List<CalendarEvent> events;

        if (currentUser.getRole() == Role.HR) {
            events = calendarEventRepository.findByStartDateGreaterThanEqualOrderByStartDateAsc(today);
        } else {
            List<Long> relevantUserIds = resolveTeamUserIds(currentUser);
            if (relevantUserIds.isEmpty()) {
                return List.of();
            }
            events = calendarEventRepository.findByUserIdInAndStartDateGreaterThanEqualOrderByStartDateAsc(
                    relevantUserIds,
                    today
            );
        }

        Map<Long, String> userNamesById = userRepository.findAllById(
                        events.stream().map(CalendarEvent::getUserId).collect(Collectors.toSet()))
                .stream()
                .collect(Collectors.toMap(User::getId, User::getFullName, (left, right) -> left));

        return events.stream()
                .map(calendarEventMapper::toResponse)
                .peek(response -> response.setUserFullName(userNamesById.get(response.getUserId())))
                .toList();
    }

    private List<Long> resolveTeamUserIds(User currentUser) {
        Set<Long> userIds = new LinkedHashSet<>();

        if (currentUser.getRole() == Role.LEADER) {
            userIds.add(currentUser.getId());
            userRepository.findByLeaderId(currentUser.getId())
                    .forEach(user -> userIds.add(user.getId()));
            return new ArrayList<>(userIds);
        }

        if (currentUser.getRole() == Role.EMPLOYEE) {
            userIds.add(currentUser.getId());
            if (currentUser.getLeaderId() != null) {
                userIds.add(currentUser.getLeaderId());
                userRepository.findByLeaderId(currentUser.getLeaderId())
                        .forEach(user -> userIds.add(user.getId()));
            }
            return new ArrayList<>(userIds);
        }

        return List.of();
    }
}

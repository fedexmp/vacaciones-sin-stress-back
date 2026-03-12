package com.vacaciones_sin_stress.user.dto.response;

import lombok.Builder;
import lombok.Getter;

/**
 * Lightweight user summary returned to HR for user-picker dropdowns.
 */
@Getter
@Builder
public class UserSummaryResponse {

    /** Database primary key — used as foreign key in vacation_balance.user_id. */
    private Long id;

    private String name;

    private String email;
}

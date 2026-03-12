package com.vacaciones_sin_stress.user.dto.response;

import com.vacaciones_sin_stress.common.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents the authenticated user's profile used by frontend guards.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserMeResponse {

    private Long id;
    private String fullName;
    private String email;
    private Role role;
    private Long leaderId;
    private boolean active;
}

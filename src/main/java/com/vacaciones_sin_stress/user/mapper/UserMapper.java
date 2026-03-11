package com.vacaciones_sin_stress.user.mapper;

import com.vacaciones_sin_stress.user.dto.response.UserMeResponse;
import com.vacaciones_sin_stress.user.entity.User;
import org.mapstruct.Mapper;

/**
 * Maps user entity to user-facing DTOs.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * Maps current user entity to /me response.
     *
     * @param user authenticated user
     * @return user profile response
     */
    UserMeResponse toMeResponse(User user);
}

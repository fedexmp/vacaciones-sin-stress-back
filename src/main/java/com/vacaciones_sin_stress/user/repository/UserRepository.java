package com.vacaciones_sin_stress.user.repository;

import com.vacaciones_sin_stress.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByClerkUserId(String clerkUserId);

    List<User> findByLeaderId(Long leaderId);
}

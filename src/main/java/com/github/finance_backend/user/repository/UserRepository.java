package com.github.finance_backend.user.repository;

import com.github.finance_backend.user.entity.UserEntity;
import com.github.finance_backend.user.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);

    // optional
    List<UserEntity> findByRole(Role role);

    List<UserEntity> findByIsActive(boolean isActive);
}

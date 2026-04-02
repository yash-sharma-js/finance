package com.github.finance_backend.user.dto;

import com.github.finance_backend.user.enums.Role;
import jakarta.persistence.Entity;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDTO {
    private Long id;
    private String name;
    private String email;
    private Role role;
    private boolean isActive;
}
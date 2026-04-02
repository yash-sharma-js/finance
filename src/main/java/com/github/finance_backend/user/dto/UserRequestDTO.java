package com.github.finance_backend.user.dto;

import jakarta.persistence.Entity;
import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class UserRequestDTO {
    private String name;
    private String email;
    private String password;
}

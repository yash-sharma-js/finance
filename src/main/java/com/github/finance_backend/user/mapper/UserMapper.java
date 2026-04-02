package com.github.finance_backend.user.mapper;

import com.github.finance_backend.user.dto.UserRequestDTO;
import com.github.finance_backend.user.dto.UserResponseDTO;
import com.github.finance_backend.user.entity.UserEntity;
import com.github.finance_backend.user.enums.Role;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class UserMapper {
    public UserEntity toEntity(UserRequestDTO dto){
        return UserEntity.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .role(Role.VIEWER)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public UserResponseDTO toDto(UserEntity entity){
        return UserResponseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .email(entity.getEmail())
                .role(entity.getRole())
                .isActive(entity.isActive())
                .build();
    }
}

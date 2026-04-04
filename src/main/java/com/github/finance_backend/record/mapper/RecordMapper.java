package com.github.finance_backend.record.mapper;


import com.github.finance_backend.record.dto.RecordRequestDTO;
import com.github.finance_backend.record.dto.RecordResponseDTO;
import com.github.finance_backend.record.entity.RecordEntity;
import com.github.finance_backend.user.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class RecordMapper {
    public RecordEntity toEntity(RecordRequestDTO dto, UserEntity user) {
        return RecordEntity.builder()
                .amount(dto.getAmount())
                .type(dto.getType())
                .category(dto.getCategory())
                .date(dto.getDate())
                .note(dto.getNote())
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public RecordResponseDTO toResponse(RecordEntity record) {
        return RecordResponseDTO.builder()
                .id(record.getId())
                .amount(record.getAmount())
                .type(record.getType())
                .category(record.getCategory())
                .date(record.getDate())
                .note(record.getNote())
                .createdBy(record.getUser().getEmail())
                .createdAt(record.getCreatedAt())
                .build();
    }
}

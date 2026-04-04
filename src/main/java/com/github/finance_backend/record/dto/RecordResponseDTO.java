package com.github.finance_backend.record.dto;

import com.github.finance_backend.record.enums.RecordType;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecordResponseDTO {
    private Long id;
    private Double amount;
    private RecordType type;
    private String category;
    private LocalDate date;
    private String note;
}

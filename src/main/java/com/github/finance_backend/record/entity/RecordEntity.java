package com.github.finance_backend.record.entity;

import com.github.finance_backend.record.enums.RecordType;
import com.github.finance_backend.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;


import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecordEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double amount;

    @Enumerated(EnumType.STRING)
    private RecordType type;

    private String category;

    private LocalDate date;

    private String note;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Builder.Default
    private boolean deleted = false;

    private LocalDateTime createdAt;
}

package com.github.finance_backend.record.repository;

import com.github.finance_backend.record.entity.RecordEntity;
import com.github.finance_backend.record.enums.RecordType;
import com.github.finance_backend.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface RecordRepository extends JpaRepository<RecordEntity, Long> {
    List<RecordEntity> findByUser(UserEntity user);

    // optional filters
    List<RecordEntity> findByUserAndType(UserEntity user, RecordType type);

    List<RecordEntity> findByUserAndDateBetween(UserEntity user, LocalDate start, LocalDate end);

    List<RecordEntity> findByUserAndCategory(UserEntity user, String category);
}

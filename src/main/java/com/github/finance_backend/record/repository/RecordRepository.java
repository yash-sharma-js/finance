package com.github.finance_backend.record.repository;

import com.github.finance_backend.record.entity.RecordEntity;
import com.github.finance_backend.record.enums.RecordType;
import com.github.finance_backend.user.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface RecordRepository extends JpaRepository<RecordEntity, Long> {

    // Non-deleted records for a user
    List<RecordEntity> findByUserAndDeletedFalse(UserEntity user);

    // Filters (excluding soft-deleted)
    List<RecordEntity> findByUserAndTypeAndDeletedFalse(UserEntity user, RecordType type);

    List<RecordEntity> findByUserAndDateBetweenAndDeletedFalse(UserEntity user, LocalDate start, LocalDate end);

    List<RecordEntity> findByUserAndCategoryAndDeletedFalse(UserEntity user, String category);

    // Paginated + combined filter query
    @Query("SELECT r FROM RecordEntity r WHERE r.user = :user AND r.deleted = false " +
            "AND (:type IS NULL OR r.type = :type) " +
            "AND (:category IS NULL OR r.category = :category) " +
            "AND (:startDate IS NULL OR r.date >= :startDate) " +
            "AND (:endDate IS NULL OR r.date <= :endDate)")
    Page<RecordEntity> findByFilters(UserEntity user, RecordType type, String category,
                                     LocalDate startDate, LocalDate endDate, Pageable pageable);

    // All non-deleted records (admin)
    List<RecordEntity> findByDeletedFalse();
}

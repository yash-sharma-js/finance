package com.github.finance_backend.dashboard.repository;


import com.github.finance_backend.record.entity.RecordEntity;
import com.github.finance_backend.record.enums.RecordType;
import com.github.finance_backend.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DashboardRepository extends JpaRepository<RecordEntity, Long> {

    @Query("SELECT SUM(r.amount) FROM RecordEntity r WHERE r.user = :user AND r.type = :type AND r.deleted = false")
    Double getTotalByType(UserEntity user, RecordType type);

    @Query("SELECT r.category, SUM(r.amount) FROM RecordEntity r WHERE r.user = :user AND r.deleted = false GROUP BY r.category")
    List<Object[]> getCategorySummary(UserEntity user);

    List<RecordEntity> findTop5ByUserAndDeletedFalseOrderByDateDesc(UserEntity user);

    // Monthly trends — returns [year, month, type, total]
    @Query("SELECT YEAR(r.date), MONTH(r.date), r.type, SUM(r.amount) " +
            "FROM RecordEntity r WHERE r.user = :user AND r.deleted = false " +
            "GROUP BY YEAR(r.date), MONTH(r.date), r.type " +
            "ORDER BY YEAR(r.date) DESC, MONTH(r.date) DESC")
    List<Object[]> getMonthlyTrends(UserEntity user);
}

package com.github.finance_backend.dashboard.repository;


import com.github.finance_backend.record.entity.RecordEntity;
import com.github.finance_backend.record.enums.RecordType;
import com.github.finance_backend.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DashboardRepository extends JpaRepository<RecordEntity, Long> {
    @Query("SELECT SUM(r.amount) FROM RecordEntity r WHERE r.user = :user AND r.type = :type")
    Double getTotalByType(UserEntity user, RecordType type);

    @Query("SELECT r.category, SUM(r.amount) FROM RecordEntity r WHERE r.user = :user GROUP BY r.category")
    List<Object[]> getCategorySummary(UserEntity user);

    List<RecordEntity> findTop5ByUserOrderByDateDesc(UserEntity user);
}

package com.github.finance_backend.dashboard.service;

import com.github.finance_backend.dashboard.repository.DashboardRepository;
import com.github.finance_backend.record.dto.RecordResponseDTO;
import com.github.finance_backend.record.enums.RecordType;
import com.github.finance_backend.record.mapper.RecordMapper;
import com.github.finance_backend.user.entity.UserEntity;
import com.github.finance_backend.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DashboardService {
    @Autowired
    private DashboardRepository dashboardRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RecordMapper recordMapper;

    public Map<String, Object> getSummary(String email) {

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Double income = Optional.ofNullable(
                dashboardRepository.getTotalByType(user, RecordType.INCOME)).orElse(0.0);

        Double expense = Optional.ofNullable(
                dashboardRepository.getTotalByType(user, RecordType.EXPENSE)).orElse(0.0);

        Double net = income - expense;

        Map<String, Object> result = new HashMap<>();
        result.put("totalIncome", income);
        result.put("totalExpense", expense);
        result.put("netBalance", net);

        return result;
    }

    public List<Map<String, Object>> getCategorySummary(String email) {

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Object[]> data = dashboardRepository.getCategorySummary(user);

        List<Map<String, Object>> result = new ArrayList<>();

        for (Object[] row : data) {
            Map<String, Object> map = new HashMap<>();
            map.put("category", row[0]);
            map.put("total", row[1]);
            result.add(map);
        }

        return result;
    }

    public List<RecordResponseDTO> getRecentRecords(String email) {

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return dashboardRepository.findTop5ByUserOrderByDateDesc(user)
                .stream()
                .map(recordMapper::toResponse)
                .toList();
    }
}

package com.github.finance_backend.dashboard.controller;

import com.github.finance_backend.dashboard.service.DashboardService;
import com.github.finance_backend.record.dto.RecordResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    private String getEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getSummary() {
        return ResponseEntity.ok(dashboardService.getSummary(getEmail()));
    }

    @GetMapping("/categories")
    public ResponseEntity<List<Map<String, Object>>> getCategories() {
        return ResponseEntity.ok(dashboardService.getCategorySummary(getEmail()));
    }

    @GetMapping("/recent")
    public ResponseEntity<List<RecordResponseDTO>> getRecent() {
        return ResponseEntity.ok(dashboardService.getRecentRecords(getEmail()));
    }
}

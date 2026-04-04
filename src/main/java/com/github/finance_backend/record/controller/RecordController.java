package com.github.finance_backend.record.controller;


import com.github.finance_backend.record.dto.RecordRequestDTO;
import com.github.finance_backend.record.dto.RecordResponseDTO;
import com.github.finance_backend.record.enums.RecordType;
import com.github.finance_backend.record.service.RecordService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/records")
public class RecordController {
    @Autowired
    private RecordService recordService;

    private String getCurrentUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    // ✅ Create — Admin only
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RecordResponseDTO> create(@Valid @RequestBody RecordRequestDTO request) {
        return ResponseEntity.ok(recordService.createRecord(request, getCurrentUserEmail()));
    }

    // ✅ Get my records — Analyst and Admin
    @GetMapping
    @PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
    public ResponseEntity<List<RecordResponseDTO>> getMyRecords() {
        return ResponseEntity.ok(recordService.getUserRecords(getCurrentUserEmail()));
    }

    // ✅ Filtered + paginated records — Analyst and Admin
    @GetMapping("/filter")
    @PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> getFilteredRecords(
            @RequestParam(required = false) RecordType type,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(
                recordService.getFilteredRecords(getCurrentUserEmail(), type, category, startDate, endDate, page, size));
    }

    // ✅ Admin: get all records
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RecordResponseDTO>> getAll() {
        return ResponseEntity.ok(recordService.getAllRecords());
    }

    // ✅ Update — Admin only
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RecordResponseDTO> update(@PathVariable Long id,
                                                    @Valid @RequestBody RecordRequestDTO request) {
        return ResponseEntity.ok(recordService.updateRecord(id, request, getCurrentUserEmail()));
    }

    // ✅ Delete (soft) — Admin only
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        recordService.deleteRecord(id, getCurrentUserEmail());
        return ResponseEntity.ok("Deleted successfully");
    }
}

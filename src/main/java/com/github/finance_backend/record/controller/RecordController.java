package com.github.finance_backend.record.controller;


import com.github.finance_backend.record.dto.RecordRequestDTO;
import com.github.finance_backend.record.dto.RecordResponseDTO;
import com.github.finance_backend.record.service.RecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/records")
public class RecordController {
    @Autowired
    private RecordService recordService;

    private String getCurrentUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    // ✅ Create
    @PostMapping
    public ResponseEntity<RecordResponseDTO> create(@RequestBody RecordRequestDTO request) {
        return ResponseEntity.ok(recordService.createRecord(request, getCurrentUserEmail()));
    }

    // ✅ Get my records
    @GetMapping
    public ResponseEntity<List<RecordResponseDTO>> getMyRecords() {
        return ResponseEntity.ok(recordService.getUserRecords(getCurrentUserEmail()));
    }

    // ✅ Admin: get all
    @GetMapping("/all")
    public ResponseEntity<List<RecordResponseDTO>> getAll() {
        return ResponseEntity.ok(recordService.getAllRecords());
    }

    // ✅ Update
    @PutMapping("/{id}")
    public ResponseEntity<RecordResponseDTO> update(@PathVariable Long id,
                                                    @RequestBody RecordRequestDTO request) {
        return ResponseEntity.ok(recordService.updateRecord(id, request, getCurrentUserEmail()));
    }

    // ✅ Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        recordService.deleteRecord(id, getCurrentUserEmail());
        return ResponseEntity.ok("Deleted successfully");
    }
}

package com.github.finance_backend.record.service;

import com.github.finance_backend.record.dto.RecordRequestDTO;
import com.github.finance_backend.record.dto.RecordResponseDTO;
import com.github.finance_backend.record.entity.RecordEntity;
import com.github.finance_backend.record.enums.RecordType;
import com.github.finance_backend.record.mapper.RecordMapper;
import com.github.finance_backend.record.repository.RecordRepository;
import com.github.finance_backend.user.entity.UserEntity;
import com.github.finance_backend.user.enums.Role;
import com.github.finance_backend.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RecordService {

    @Autowired
    private RecordRepository recordRepository;

    @Autowired
    private RecordMapper recordMapper;

    @Autowired
    private UserRepository userRepository;

    // ✅ Create
    public RecordResponseDTO createRecord(RecordRequestDTO request, String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        RecordEntity record = recordMapper.toEntity(request, user);
        return recordMapper.toResponse(recordRepository.save(record));
    }

    // ✅ Get all records of user (non-deleted)
    public List<RecordResponseDTO> getUserRecords(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return recordRepository.findByUserAndDeletedFalse(user)
                .stream()
                .map(recordMapper::toResponse)
                .collect(Collectors.toList());
    }

    // ✅ Get all non-deleted records (admin use)
    public List<RecordResponseDTO> getAllRecords() {
        return recordRepository.findByDeletedFalse()
                .stream()
                .map(recordMapper::toResponse)
                .collect(Collectors.toList());
    }

    // ✅ Filtered + paginated records
    public Map<String, Object> getFilteredRecords(String email, RecordType type, String category,
                                                   LocalDate startDate, LocalDate endDate,
                                                   int page, int size) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());
        Page<RecordEntity> recordPage = recordRepository.findByFilters(user, type, category, startDate, endDate, pageable);

        List<RecordResponseDTO> content = recordPage.getContent()
                .stream()
                .map(recordMapper::toResponse)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("content", content);
        response.put("totalElements", recordPage.getTotalElements());
        response.put("totalPages", recordPage.getTotalPages());
        response.put("currentPage", recordPage.getNumber());
        response.put("pageSize", recordPage.getSize());

        return response;
    }

    // ✅ Soft Delete (role-aware: admin can delete any record)
    public void deleteRecord(Long id, String email) {
        RecordEntity record = recordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found"));

        UserEntity currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Admin can delete any record; others can only delete their own
        if (currentUser.getRole() != Role.ADMIN && !record.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized: You can only delete your own records");
        }

        record.setDeleted(true);
        recordRepository.save(record);
    }

    // ✅ Update (role-aware: admin can update any record)
    public RecordResponseDTO updateRecord(Long id, RecordRequestDTO request, String email) {
        RecordEntity record = recordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found"));

        if (record.isDeleted()) {
            throw new RuntimeException("Cannot update a deleted record");
        }

        UserEntity currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Admin can update any record; others can only update their own
        if (currentUser.getRole() != Role.ADMIN && !record.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized: You can only update your own records");
        }

        record.setAmount(request.getAmount());
        record.setType(request.getType());
        record.setCategory(request.getCategory());
        record.setDate(request.getDate());
        record.setNote(request.getNote());

        return recordMapper.toResponse(recordRepository.save(record));
    }
}
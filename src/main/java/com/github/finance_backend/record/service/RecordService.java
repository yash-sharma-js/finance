package com.github.finance_backend.record.service;

import com.github.finance_backend.record.dto.RecordRequestDTO;
import com.github.finance_backend.record.dto.RecordResponseDTO;
import com.github.finance_backend.record.entity.RecordEntity;
import com.github.finance_backend.record.mapper.RecordMapper;
import com.github.finance_backend.record.repository.RecordRepository;
import com.github.finance_backend.user.entity.UserEntity;
import com.github.finance_backend.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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

    // ✅ Get all records of user
    public List<RecordResponseDTO> getUserRecords(String email) {

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return recordRepository.findByUser(user)
                .stream()
                .map(recordMapper::toResponse)
                .collect(Collectors.toList());
    }

    // ✅ Get all records (admin use)
    public List<RecordResponseDTO> getAllRecords() {
        return recordRepository.findAll()
                .stream()
                .map(recordMapper::toResponse)
                .collect(Collectors.toList());
    }

    // ✅ Delete
    public void deleteRecord(Long id, String email) {

        RecordEntity record = recordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found"));

        if (!record.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized");
        }

        recordRepository.delete(record);
    }

    // ✅ Update
    public RecordResponseDTO updateRecord(Long id, RecordRequestDTO request, String email) {

        RecordEntity record = recordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found"));

        if (!record.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized");
        }

        record.setAmount(request.getAmount());
        record.setType(request.getType());
        record.setCategory(request.getCategory());
        record.setDate(request.getDate());
        record.setNote(request.getNote());

        return recordMapper.toResponse(recordRepository.save(record));
    }
}
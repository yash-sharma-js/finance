package com.github.finance_backend.user.service;


import com.github.finance_backend.user.dto.UserRequestDTO;
import com.github.finance_backend.user.dto.UserResponseDTO;
import com.github.finance_backend.user.entity.UserEntity;
import com.github.finance_backend.user.mapper.UserMapper;
import com.github.finance_backend.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;

    public UserResponseDTO register(UserRequestDTO request){
        userRepository.findByEmail(request.getEmail())
                .ifPresent(u -> {
                    throw new RuntimeException("Email already exists");
                });
        UserEntity user = userMapper.toEntity(request);
        UserEntity savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }
}

package com.github.finance_backend.user.service;


import com.github.finance_backend.config.AppConfig;
import com.github.finance_backend.security.JwtUtil;
import com.github.finance_backend.user.dto.UserRequestDTO;
import com.github.finance_backend.user.dto.UserResponseDTO;
import com.github.finance_backend.user.entity.UserEntity;
import com.github.finance_backend.user.mapper.UserMapper;
import com.github.finance_backend.user.repository.UserRepository;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtUtil jwtUtil;

    public UserResponseDTO register(UserRequestDTO request){
        userRepository.findByEmail(request.getEmail())
                .ifPresent(u -> {
                    throw new RuntimeException("Email already exists");
                });
        UserEntity user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        UserEntity savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    public String login(UserRequestDTO request){
        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        return jwtUtil.generateToken(user.getEmail(), user.getRole().name());
    }


    public List<UserResponseDTO> getUsers(){
        return null;
    }

}

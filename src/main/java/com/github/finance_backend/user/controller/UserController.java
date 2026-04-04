package com.github.finance_backend.user.controller;


import com.github.finance_backend.user.dto.LoginRequestDTO;
import com.github.finance_backend.user.dto.UserRequestDTO;
import com.github.finance_backend.user.dto.UserResponseDTO;
import com.github.finance_backend.user.enums.Role;
import com.github.finance_backend.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1")
@RestController
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/test")
    public String test() {
        return "200 Status";
    }

    // ===== Public Auth Endpoints =====

    @PostMapping("/auth/register")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody UserRequestDTO request) {
        return ResponseEntity.ok(userService.register(request));
    }

    @PostMapping("/auth/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(userService.login(request));
    }

    // ===== Admin-Only User Management =====

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getUsers());
    }

    @GetMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PatchMapping("/users/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> updateRole(@PathVariable Long id, @RequestParam String role) {
        Role parsedRole = Role.valueOf(role.toUpperCase());
        return ResponseEntity.ok(userService.updateRole(id, parsedRole));
    }

    @PatchMapping("/users/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> updateStatus(@PathVariable Long id, @RequestParam boolean active) {
        return ResponseEntity.ok(userService.updateStatus(id, active));
    }
}

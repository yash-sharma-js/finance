package com.github.finance_backend.user.controller;


import com.github.finance_backend.user.dto.UserRequestDTO;
import com.github.finance_backend.user.dto.UserResponseDTO;
import com.github.finance_backend.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1")
@RestController
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/test")
    public String test(){
        return "200 Status";
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRequestDTO request) {
        try {
            return ResponseEntity.ok(userService.register(request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public String login(@RequestBody UserRequestDTO request){
        return null;
    }

    @GetMapping("/users")
    public Object getAllUsers() {
        return null;
    }

    @PatchMapping("/users/{id}/role")
    public UserResponseDTO updateRole(@PathVariable Long id, @RequestParam String role) {
        return null;
    }

}

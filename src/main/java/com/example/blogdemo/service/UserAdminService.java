package com.example.blogdemo.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.blogdemo.dto.UserResponse;
import com.example.blogdemo.entity.Role;
import com.example.blogdemo.entity.User;
import com.example.blogdemo.repository.UserRepository;

@Service
public class UserAdminService {

    private final UserRepository userRepository;

    public UserAdminService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserResponse> listUsers() {
        return userRepository.findAll().stream()
                .map(UserResponse::from)
                .toList();
    }

    public UserResponse updateRole(Long id, RoleRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("request body is required");
        }
        if (request.role() == null || request.role().isBlank()) {
            throw new IllegalArgumentException("role is required");
        }
        Role role;
        try {
            role = Role.valueOf(request.role().trim().toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("role must be USER, ADMIN, or SUPER_ADMIN");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + id));
        user.setRole(role);
        return UserResponse.from(userRepository.save(user));
    }

    public record RoleRequest(String role) {
    }
}

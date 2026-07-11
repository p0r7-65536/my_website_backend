package com.example.blogdemo.dto;

import java.time.LocalDateTime;

import com.example.blogdemo.entity.Role;
import com.example.blogdemo.entity.User;

public record UserResponse(
        Long id,
        String username,
        String email,
        Role role,
        String avatar,
        LocalDateTime createdAt) {

    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getAvatar(),
                user.getCreatedAt());
    }
}

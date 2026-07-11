package com.example.blogdemo.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.blogdemo.common.ApiResponse;
import com.example.blogdemo.dto.UserResponse;
import com.example.blogdemo.service.AuthService;
import com.example.blogdemo.service.AuthService.LoginRequest;
import com.example.blogdemo.service.AuthService.LoginResponse;
import com.example.blogdemo.service.AuthService.RegisterRequest;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ApiResponse<UserResponse> register(@RequestBody RegisterRequest request) {
        return ApiResponse.success(authService.register(request));
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request) {
        return ApiResponse.success(authService.login(request));
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<UserResponse> me() {
        return ApiResponse.success(authService.me());
    }
}

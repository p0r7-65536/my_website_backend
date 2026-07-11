package com.example.blogdemo.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.blogdemo.common.ApiResponse;
import com.example.blogdemo.dto.UserResponse;
import com.example.blogdemo.service.UserAdminService;
import com.example.blogdemo.service.UserAdminService.RoleRequest;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserAdminService userAdminService;

    public UserController(UserAdminService userAdminService) {
        this.userAdminService = userAdminService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ApiResponse<List<UserResponse>> listUsers() {
        return ApiResponse.success(userAdminService.listUsers());
    }

    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ApiResponse<UserResponse> updateRole(@PathVariable Long id, @RequestBody RoleRequest request) {
        return ApiResponse.success(userAdminService.updateRole(id, request));
    }
}

package com.example.blogdemo.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import com.example.blogdemo.entity.Role;
import com.example.blogdemo.entity.User;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static User currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof ForumUserDetails userDetails)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        return userDetails.getUser();
    }

    public static boolean hasAdminPower(User user) {
        return user.getRole() == Role.ADMIN || user.getRole() == Role.SUPER_ADMIN;
    }
}

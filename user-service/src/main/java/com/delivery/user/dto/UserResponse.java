package com.delivery.user.dto;

import com.delivery.user.entity.User;
import com.delivery.user.entity.UserRole;

public record UserResponse(
        Long id,
        String email,
        String name,
        String role
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRole().name()
        );
    }
}
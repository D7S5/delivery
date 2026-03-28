package com.delivery.user.dto;

import com.delivery.user.entity.UserRole;

public record SignupResponse(
        Long userId,
        String email,
        String name,
        UserRole role
){
}

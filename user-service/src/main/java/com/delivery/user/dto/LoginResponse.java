package com.delivery.user.dto;

public record LoginResponse(
        String accessToken,
        UserResponse user
) {
}
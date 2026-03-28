package com.delivery.user.dto;

public record CreateRiderClientRequest(
        Long userId,
        String riderName
) {
}

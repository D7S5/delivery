package com.example.riderservice.dto;

public record CreateRiderRequest(
        Long userId,
        String riderName
) {
}

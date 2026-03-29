package com.example.riderservice.dto;

import com.example.riderservice.entity.RiderStatus;

public record RiderStatusResponse(
        Long riderId,
        Long userId,
        RiderStatus Status,
        boolean online
) {
}

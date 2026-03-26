package com.example.riderservice.dto;

public record DispatchResponse(
        Long assignmentId,
        Long orderReceiveId,
        Long riderId,
        String message
) {
}

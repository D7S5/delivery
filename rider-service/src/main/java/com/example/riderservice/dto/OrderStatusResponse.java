package com.example.riderservice.dto;

public record OrderStatusResponse(
        Long orderId,
        String status
) {

}

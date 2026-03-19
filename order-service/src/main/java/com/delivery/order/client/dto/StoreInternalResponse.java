package com.delivery.order.client.dto;
public record StoreInternalResponse(
        Long id,
        String name,
        Integer minOrderAmount,
        StoreStatus status
) {
}

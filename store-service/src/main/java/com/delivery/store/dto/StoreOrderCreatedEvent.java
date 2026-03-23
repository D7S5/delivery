package com.delivery.store.dto;
import java.time.LocalDateTime;

import java.util.List;

public record StoreOrderCreatedEvent(
        String eventId,
        Long orderId,
        Long customerId,
        String customerEmail,
        Long storeId,
        String storeName,
        String deliveryAddress,
        Integer totalAmount,
        String requestMessage,
        LocalDateTime createdAt,
        List<StoreOrderItemDto> items
) {
}

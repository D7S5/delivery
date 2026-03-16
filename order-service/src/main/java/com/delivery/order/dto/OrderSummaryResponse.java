package com.delivery.order.dto;

import com.delivery.order.entity.Order;
import com.delivery.order.entity.OrderStatus;

import java.time.LocalDateTime;

public record OrderSummaryResponse(
        Long orderId,
        Long storeId,
        String storeName,
        Integer totalAmount,
        OrderStatus status,
        LocalDateTime createdAt
) {
    public static OrderSummaryResponse from(Order order) {
        return new OrderSummaryResponse(
                order.getId(),
                order.getStoreId(),
                order.getStoreName(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getCreatedAt()
        );
    }
}

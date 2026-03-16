package com.delivery.order.dto;

import com.delivery.order.entity.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public record OrderDetailResponse(
        Long orderId,
        Long customerId,
        String customerEmail,
        Long storeId,
        String storeName,
        String deliveryAddress,
        Integer totalAmount,
        OrderStatus status,
        String requestMessage,
        LocalDateTime createdAt,
        List<OrderItemResponse> items
) {
}

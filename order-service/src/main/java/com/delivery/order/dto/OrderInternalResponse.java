package com.delivery.order.dto;

import com.delivery.order.entity.Order;
import com.delivery.order.entity.OrderStatus;

public record OrderInternalResponse(
        Long orderId,
        Long customerId,
        String customerEmail,
        Long storeId,
        String storeName,
        Integer totalAmount,
        OrderStatus status
) {
    public static OrderInternalResponse from(Order order) {
        return new OrderInternalResponse(
                order.getId(),
                order.getCustomerId(),
                order.getCustomerEmail(),
                order.getStoreId(),
                order.getStoreName(),
                order.getTotalAmount(),
                order.getStatus()
        );
    }
}
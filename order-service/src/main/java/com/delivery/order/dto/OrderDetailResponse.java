package com.delivery.order.dto;

import com.delivery.order.entity.Order;
import com.delivery.order.entity.OrderItem;
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
    OrderDetailResponse from(Order order, List<OrderItem> items) {
        return new OrderDetailResponse(
                order.getId(),
                order.getCustomerId(),
                order.getCustomerEmail(),
                order.getStoreId(),
                order.getStoreName(),
                order.getDeliveryAddress(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getRequestMessage(),
                order.getCreatedAt(),
                items.stream().map(OrderItemResponse::from).toList());
    }
}

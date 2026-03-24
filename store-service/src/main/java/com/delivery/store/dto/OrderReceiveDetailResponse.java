package com.delivery.store.dto;

import com.delivery.store.entity.OrderReceive;
import com.delivery.store.entity.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public record OrderReceiveDetailResponse(
        Long id,
        Long orderId,
        Long customerId,
        String customerEmail,
        Long storeId,
        String storeName,
        String deliveryAddress,
        Integer totalAmount,
        String requestMessage,
        OrderStatus status,
        LocalDateTime createdAt,
        List<OrderReceiveItemResponse> items
) {
    public static OrderReceiveDetailResponse from(OrderReceive orderReceive) {
        return new OrderReceiveDetailResponse(
                orderReceive.getId(),
                orderReceive.getOrderId(),
                orderReceive.getCustomerId(),
                orderReceive.getCustomerEmail(),
                orderReceive.getStoreId(),
                orderReceive.getStoreName(),
                orderReceive.getDeliveryAddress(),
                orderReceive.getTotalAmount(),
                orderReceive.getRequestMessage(),
                orderReceive.getStatus(),
                orderReceive.getCreatedAt(),
                orderReceive.getItems().stream()
                        .map(OrderReceiveItemResponse::from)
                        .toList()
        );
    }
}

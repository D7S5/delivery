package com.delivery.store.dto;

import com.delivery.store.entity.OrderReceive;
import com.delivery.store.entity.OrderReceiveItem;
import com.delivery.store.entity.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public record OrderReceiveSummaryResponse(
        Long id,
        Long orderId,
        String customerEmail,
        String storeName,
        String deliveryAddress,
        String requestMessage,
        Double storeLat,
        Double storeLng,
        Integer totalAmount,
        OrderStatus status,
        LocalDateTime createdAt,
        List<OrderReceiveItemResponse> items
) {
    public static OrderReceiveSummaryResponse from(OrderReceive orderReceive) {
        return new OrderReceiveSummaryResponse(
                orderReceive.getId(),
                orderReceive.getOrderId(),
                orderReceive.getCustomerEmail(),
                orderReceive.getStoreName(),
                orderReceive.getDeliveryAddress(),
                orderReceive.getRequestMessage(),
                orderReceive.getStoreLat(),
                orderReceive.getStoreLng(),
                orderReceive.getTotalAmount(),
                orderReceive.getStatus(),
                orderReceive.getCreatedAt(),
                orderReceive.getItems().stream()
                        .map(OrderReceiveItemResponse::from)
                        .toList()
        );
    }
}
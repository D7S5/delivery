package com.delivery.payment.dto;

public record OrderInternalResponse(
        Long orderId,
        Long customerId,
        String customerEmail,
        Long storeId,
        String storeName,
        Integer totalAmount,
        String status
) {
}


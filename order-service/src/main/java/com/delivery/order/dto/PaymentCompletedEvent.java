package com.delivery.order.dto;

public record PaymentCompletedEvent(
        String eventId,
        Long paymentId,
        Long orderId,
        Long customerId,
        Integer amount
) {
}

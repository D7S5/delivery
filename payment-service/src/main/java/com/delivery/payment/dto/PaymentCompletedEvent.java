package com.delivery.payment.dto;

public record PaymentCompletedEvent(
        String eventId,
        Long paymentId,
        Long orderId,
        Long customerId,
        Integer amount
) {
}
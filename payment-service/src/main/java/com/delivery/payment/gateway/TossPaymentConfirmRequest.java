package com.delivery.payment.gateway;

public record TossPaymentConfirmRequest(
        String paymentKey,
        String orderId,
        Integer amount
) {
}

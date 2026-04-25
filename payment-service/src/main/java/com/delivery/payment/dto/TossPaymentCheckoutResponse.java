package com.delivery.payment.dto;

public record TossPaymentCheckoutResponse(
        Long orderId,
        String tossOrderId,
        String orderName,
        Integer amount,
        String clientKey,
        String successUrl,
        String failUrl
) {
}

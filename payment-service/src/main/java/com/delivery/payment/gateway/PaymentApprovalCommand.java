package com.delivery.payment.gateway;

public record PaymentApprovalCommand(
        Long orderId,
        Long customerId,
        String customerEmail,
        Integer amount,
        String merchantOrderId,
        String paymentKey
) {
}

package com.delivery.payment.gateway;

import com.delivery.payment.entity.PaymentMethod;

import java.time.LocalDateTime;

public record PaymentGatewayResult(
        boolean approved,
        String providerTransactionId,
        String failureReason,
        LocalDateTime approvedAt,
        PaymentMethod paymentMethod
) {
    public static PaymentGatewayResult approved(String providerTransactionId, LocalDateTime approvedAt, PaymentMethod paymentMethod) {
        return new PaymentGatewayResult(true, providerTransactionId, null, approvedAt, paymentMethod);
    }

    public static PaymentGatewayResult failed(String failureReason) {
        return new PaymentGatewayResult(false, null, failureReason, null, PaymentMethod.UNKNOWN);
    }
}

package com.delivery.payment.dto;

import com.delivery.payment.entity.Payment;
import com.delivery.payment.entity.PaymentMethod;
import com.delivery.payment.entity.PaymentStatus;

import java.time.LocalDateTime;

public record PaymentSummaryResponse(
        Long paymentId,
        Long orderId,
        Integer amount,
        PaymentMethod paymentMethod,
        PaymentStatus status,
        LocalDateTime createdAt
) {
    public static PaymentSummaryResponse from(Payment payment) {
        return new PaymentSummaryResponse(
                payment.getId(),
                payment.getOrderId(),
                payment.getAmount(),
                payment.getPaymentMethod(),
                payment.getStatus(),
                payment.getCreatedAt()
        );
    }
}

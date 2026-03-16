package com.delivery.payment.dto;

import com.delivery.payment.entity.Payment;
import com.delivery.payment.entity.PaymentMethod;
import com.delivery.payment.entity.PaymentStatus;

import java.time.LocalDateTime;

public record PaymentDetailResponse(
        Long paymentId,
        Long orderId,
        Long customerId,
        String customerEmail,
        Integer amount,
        PaymentMethod paymentMethod,
        PaymentStatus paymentStatus,
        LocalDateTime createdAt
) {
    public static PaymentDetailResponse from(Payment payment) {
        return new PaymentDetailResponse(
                payment.getId(),
                payment.getOrderId(),
                payment.getCustomerId(),
                payment.getCustomerEmail(),
                payment.getAmount(),
                payment.getPaymentMethod(),
                payment.getStatus(),
                payment.getCreatedAt()
        );
    }
}

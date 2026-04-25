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
        String merchantOrderId,
        String paymentKey,
        PaymentMethod paymentMethod,
        PaymentStatus paymentStatus,
        String provider,
        String providerTransactionId,
        String failureReason,
        LocalDateTime createdAt,
        LocalDateTime approvedAt
) {
    public static PaymentDetailResponse from(Payment payment) {
        return new PaymentDetailResponse(
                payment.getId(),
                payment.getOrderId(),
                payment.getCustomerId(),
                payment.getCustomerEmail(),
                payment.getAmount(),
                payment.getMerchantOrderId(),
                payment.getPaymentKey(),
                payment.getPaymentMethod(),
                payment.getStatus(),
                payment.getProvider(),
                payment.getProviderTransactionId(),
                payment.getFailureReason(),
                payment.getCreatedAt(),
                payment.getApprovedAt()
        );
    }
}

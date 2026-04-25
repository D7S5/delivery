package com.delivery.payment.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long orderId;

    @Column(nullable = false)
    private Long customerId;

    @Column(nullable = false, length = 100)
    private String customerEmail;

    @Column(nullable = false)
    private Integer amount;

    @Column(nullable = false, length = 64)
    private String merchantOrderId;

    @Column(nullable = false, length = 200, unique = true)
    private String paymentKey;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status;

    @Column(nullable = false, length = 50)
    private String provider;

    @Column(length = 100, unique = true)
    private String providerTransactionId;

    @Column(length = 255)
    private String failureReason;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime approvedAt;

    @Builder
    public Payment(Long orderId, Long customerId, String customerEmail, Integer amount, String merchantOrderId,
                   String paymentKey, PaymentMethod paymentMethod, PaymentStatus status, String provider,
                   String providerTransactionId, String failureReason, LocalDateTime createdAt,
                   LocalDateTime approvedAt) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.customerEmail = customerEmail;
        this.amount = amount;
        this.merchantOrderId = merchantOrderId;
        this.paymentKey = paymentKey;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.provider = provider;
        this.providerTransactionId = providerTransactionId;
        this.failureReason = failureReason;
        this.createdAt = createdAt;
        this.approvedAt = approvedAt;
    }

    public void markCompleted(String providerTransactionId, LocalDateTime approvedAt) {
        this.status = PaymentStatus.COMPLETED;
        this.providerTransactionId = providerTransactionId;
        this.failureReason = null;
        this.approvedAt = approvedAt;
    }

    public void updatePaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void markFailed(String failureReason) {
        this.status = PaymentStatus.FAILED;
        this.failureReason = failureReason;
        this.approvedAt = null;
    }
}

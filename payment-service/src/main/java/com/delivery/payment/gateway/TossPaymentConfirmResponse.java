package com.delivery.payment.gateway;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.OffsetDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TossPaymentConfirmResponse(
        String paymentKey,
        String orderId,
        String method,
        String status,
        String lastTransactionKey,
        OffsetDateTime approvedAt,
        TossEasyPay easyPay
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record TossEasyPay(
            String provider
    ) {
    }
}

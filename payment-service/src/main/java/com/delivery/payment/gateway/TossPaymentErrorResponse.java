package com.delivery.payment.gateway;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TossPaymentErrorResponse(
        String code,
        String message
) {
}

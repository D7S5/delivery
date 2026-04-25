package com.delivery.payment.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.delivery.payment.entity.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreatePaymentRequest(
        @NotNull(message = "주문 ID는 필수입니다.")
        Long orderId,

        @JsonAlias({"merchantOrderId", "merchant_order_id"})
        @NotBlank(message = "주문번호는 필수입니다.")
        String tossOrderId,

        @JsonAlias({"paymentkey", "payment_key"})
        String paymentKey,

        @JsonAlias({"payment_method", "method"})
        PaymentMethod paymentMethod,

        @JsonAlias({"kakao_tid", "tid"})
        String kakaoTid,

        @JsonAlias({"pg_token", "kakao_pg_token"})
        String kakaoPgToken,

        Integer amount
) {
    public CreatePaymentRequest(Long orderId, String tossOrderId, String paymentKey, Integer amount) {
        this(orderId, tossOrderId, paymentKey, null, null, null, amount);
    }
}

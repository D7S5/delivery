package com.delivery.payment.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreatePaymentRequest(
        @NotNull(message = "주문 ID는 필수입니다.")
        Long orderId,

        @NotBlank(message = "토스 주문번호는 필수입니다.")
        String tossOrderId,

        @JsonAlias({"paymentkey", "payment_key"})
        @NotBlank(message = "paymentKey는 필수입니다. 토스 결제 완료 후 받은 paymentKey를 요청 body에 포함해주세요.")
        String paymentKey,

        Integer amount
) {
}

package com.delivery.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateOrderRequest(

        @NotNull(message = "가게 ID는 필수입니다.")
        Long storeId,

        @NotBlank(message = "가게 이름은 필수입니다.")
        @Size(max = 100, message = "가게 이름은 100자 이하여야 합니다.")
        String storeName,

        @NotBlank(message = "배달 주소는 필수입니다.")
        @Size(max = 255, message = "배달 주소는 255자 이하여야 합니다.")
        String deliveryAddress,

        @Size(max = 500, message = "요청사항은 500자 이하여야 합니다.")
        String requestMessage,

        @Valid
        @NotEmpty(message = "주문 상품은 1개 이상이어야 합니다.")
        List<CreateOrderItemRequest> items
) {
}
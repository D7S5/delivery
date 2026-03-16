package com.delivery.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateOrderItemRequest(
        @NotNull(message = "메뉴 ID는 필수입니다")
        Long menuId,
        @NotBlank(message = "메뉴 이름은 필수입니다.")
        @Size(message = "메뉴 이름은 100자 이하여야합니다.")
        String menuName,
        @NotNull(message = "메뉴 가격은 필수입니다.")
        @Min(value = 0, message = "메뉴 가격은 0 이상이여야합니다.")
        Integer menuPrice,

        @NotNull(message = "수량은 필수입니다.")
        @Min(value = 1, message = "수량은 1 이상이여야 합니다.")
        Integer quantity
) {
}

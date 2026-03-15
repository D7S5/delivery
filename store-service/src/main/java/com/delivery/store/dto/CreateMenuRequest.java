package com.delivery.store.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateMenuRequest(

        @NotBlank(message = "메뉴 이름은 필수입니다.")
        @Size(max = 100, message = "메뉴 이름은 100자 이하여야 합니다.")
        String name,

        @Min(value = 0, message = "가격은 0 이상이어야 합니다.")
        Integer price,

        @NotBlank(message = "메뉴 설명은 필수입니다.")
        @Size(max = 255, message = "메뉴 설명은 255자 이하여야 합니다.")
        String description
) {
}

package com.delivery.store.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateMenuRequest(

        @NotBlank(message = "메뉴 이름은 필수입니다.")
        @Size(max = 30, message = "메뉴 이름은 30자 이하여야 합니다.")
        String name,

        @Min(value = 0, message = "가격은 0 이상이어야 합니다.")
        Integer price,

        @NotBlank(message = "메뉴 설명은 필수입니다.")
        @Size(max = 100, message = "메뉴 설명은 100자 이하여야 합니다.")
        String description,

        @NotNull(message = "품절 여부는 필수입니다.")
        Boolean soldOut
) {
}
package com.delivery.store.dto;

import com.delivery.store.entity.StoreStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateStoreRequest(

        @NotBlank(message = "가게 이름은 필수입니다.")
        @Size(max = 30, message = "가게 이름은 30자 이하여야 합니다.")
        String name,

        @NotBlank(message = "주소는 필수입니다.")
        @Size(max = 50, message = "주소는 50자 이하여야 합니다.")
        String address,

        @NotBlank(message = "전화번호는 필수입니다.")
        @Size(max = 30, message = "전화번호는 30자 이하여야 합니다.")
        String phoneNumber,

        Double storeLat,
        Double storeLng,

        @Min(value = 0, message = "최소주문금액은 0 이상이어야 합니다.")
        Integer minOrderAmount,

        @NotNull(message = "가게 상태는 필수입니다.")
        StoreStatus status
) {
}
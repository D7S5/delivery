package com.delivery.store.dto;

public record OrderItemResponse(
        Long id,
        Long menuId,
        String menuName,
        Integer menuPrice,
        Integer quantity,
        Integer itemTotalPrice) {

}

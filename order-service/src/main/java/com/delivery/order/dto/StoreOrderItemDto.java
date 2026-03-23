package com.delivery.order.dto;

public record StoreOrderItemDto(
        Long menuId,
        String menuName,
        Integer menuPrice,
        Integer quantity
) {
}
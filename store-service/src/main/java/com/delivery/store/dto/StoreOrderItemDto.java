package com.delivery.store.dto;


public record StoreOrderItemDto(
        Long menuId,
        String menuName,
        Integer menuPrice,
        Integer quantity
) {
}
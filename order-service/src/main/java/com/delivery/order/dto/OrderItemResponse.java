package com.delivery.order.dto;

import com.delivery.order.entity.OrderItem;

public record OrderItemResponse(
        Long id,
        Long menuId,
        String menuName,
        Integer menuPrice,
        Integer quantity,
        Integer itemTotalPrice) {
    public static OrderItemResponse from(OrderItem orderItem) {
        return new OrderItemResponse(
                orderItem.getId(),
                orderItem.getMenuId(),
                orderItem.getMenuName(),
                orderItem.getMenuPrice(),
                orderItem.getQuantity(),
                orderItem.getItemTotalPrice()
        );
    }
}

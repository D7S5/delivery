package com.delivery.store.dto;
import com.delivery.store.entity.OrderReceiveItem;

public record OrderReceiveItemResponse(
        Long id,
        Long menuId,
        String menuName,
        Integer menuPrice,
        Integer quantity
) {
    public static OrderReceiveItemResponse from(OrderReceiveItem item) {
        return new OrderReceiveItemResponse(
                item.getId(),
                item.getMenuId(),
                item.getMenuName(),
                item.getMenuPrice(),
                item.getQuantity()
        );
    }
}

package com.delivery.store.dto;

import com.delivery.store.entity.Menu;

public record MenuResponse(
        Long id,
        Long storeId,
        String name,
        Integer price,
        String description,
        Boolean soldOut
) {
    public static MenuResponse from(Menu menu) {
        return new MenuResponse(
                menu.getId(),
                menu.getStoreId(),
                menu.getName(),
                menu.getPrice(),
                menu.getDescription(),
                menu.getSoldOut()
        );
    }
}
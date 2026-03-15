package com.delivery.store.dto;

import com.delivery.store.entity.Store;
import com.delivery.store.entity.StoreStatus;

public record StoreListResponse(
        Long id,
        String name,
        String address,
        Integer minOrderAmount,
        StoreStatus status
) {
    public static StoreListResponse from(Store store) {
        return new StoreListResponse(
                store.getId(),
                store.getName(),
                store.getAddress(),
                store.getMinOrderAmount(),
                store.getStatus()
        );
    }
}
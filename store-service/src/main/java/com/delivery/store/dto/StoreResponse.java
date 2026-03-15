package com.delivery.store.dto;
import com.delivery.store.entity.Store;
import com.delivery.store.entity.StoreStatus;

public record StoreResponse(
        Long id,
        Long ownerId,
        String ownerEmail,
        String name,
        String address,
        String phoneNumber,
        Integer minOrderAmount,
        StoreStatus status
) {
    public static StoreResponse from(Store store) {
        return new StoreResponse(
                store.getId(),
                store.getOwnerId(),
                store.getOwnerEmail(),
                store.getName(),
                store.getAddress(),
                store.getPhoneNumber(),
                store.getMinOrderAmount(),
                store.getStatus()
        );
    }
}

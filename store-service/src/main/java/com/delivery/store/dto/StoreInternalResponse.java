package com.delivery.store.dto;

import com.delivery.store.entity.StoreStatus;

public record StoreInternalResponse(Long id,
                                    String name,
                                    Integer minOrderAmount,
                                    StoreStatus status) {
}

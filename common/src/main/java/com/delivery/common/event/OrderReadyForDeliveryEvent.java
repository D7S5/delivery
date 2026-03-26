package com.delivery.common.event;

import java.time.LocalDateTime;

public record OrderReadyForDeliveryEvent(
        Long orderId,
        Long orderReceiveId,
        Long storeId,
        Double storeLat,
        Double storeLng,
        String deliveryAddress,
        LocalDateTime occurredAt
) {
}

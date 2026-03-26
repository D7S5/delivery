package com.delivery.common.event;

import java.time.LocalDateTime;

public record DeliveryCompletedEvent(
        Long orderId,
        Long orderReceiveId,
        Long riderId,
        LocalDateTime occurredAt
) {
}

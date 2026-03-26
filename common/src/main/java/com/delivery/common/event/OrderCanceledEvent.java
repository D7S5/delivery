package com.delivery.common.event;

import java.time.LocalDateTime;

public record OrderCanceledEvent(
        Long orderId,
        Long orderReceiveId,
        String reason,
        LocalDateTime occurredAt
) {

}

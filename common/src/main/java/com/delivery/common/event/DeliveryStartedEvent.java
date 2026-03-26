package com.delivery.common.event;

import java.time.LocalDateTime;

public record DeliveryStartedEvent(
        Long orderId,
        Long orderReceiveId,
        Long riderId,
        LocalDateTime occurredAt

) { }

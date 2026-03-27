package com.delivery.store.producer;

import com.delivery.common.event.OrderReadyForDeliveryEvent;
import com.delivery.store.entity.OrderReceive;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class OrderReadyForDeliveryProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publish(OrderReceive orderReceive) {
        OrderReadyForDeliveryEvent event = new OrderReadyForDeliveryEvent(
                orderReceive.getOrderId(),
                orderReceive.getId(),
                orderReceive.getStoreId(),
                orderReceive.getStoreLat(),
                orderReceive.getStoreLng(),
                orderReceive.getDeliveryAddress(),
                LocalDateTime.now()
        );

        kafkaTemplate.send(
                "order.ready-for-delivery",
                String.valueOf(orderReceive.getOrderId()),
                event
        );
    }
}
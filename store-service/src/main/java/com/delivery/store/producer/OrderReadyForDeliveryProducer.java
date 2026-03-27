package com.delivery.store.producer;

import com.delivery.common.event.OrderReadyForDeliveryEvent;
import com.delivery.store.entity.OrderReceive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderReadyForDeliveryProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publish(OrderReceive orderReceive) {
        log.info("=== publish 시작 ===");

        OrderReadyForDeliveryEvent event = new OrderReadyForDeliveryEvent(
                orderReceive.getOrderId(),
                orderReceive.getId(),
                orderReceive.getStoreId(),
                orderReceive.getStoreLat(),
                orderReceive.getStoreLng(),
                orderReceive.getDeliveryAddress(),
                LocalDateTime.now()
        );

        try {
            kafkaTemplate.send(
                    "order.ready-for-delivery",
                    String.valueOf(orderReceive.getOrderId()),
                    event
            ).get();
            log.info("Kafka 전송 완료");
        } catch (Exception e) {
            log.error("Kafka 전송 실패", e);
            throw new RuntimeException(e);
        }
    }
}
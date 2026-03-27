package com.delivery.store.service;

import com.delivery.common.event.OrderCanceledEvent;
import com.delivery.common.event.OrderReadyForDeliveryEvent;
import com.delivery.store.entity.OrderReceive;
import com.delivery.store.entity.OrderStatus;
import com.delivery.store.repository.OrderReceiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class StoreOrderService {

    private final OrderReceiveRepository orderReceiveRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static String topic = "order.ready-for-delivery";

    @Transactional
    public void markReadyForDelivery(Long orderReceiveId) {
        OrderReceive order = orderReceiveRepository.findById(orderReceiveId)
                .orElseThrow(() -> new IllegalArgumentException("주문이 없습니다."));

        if (order.getStatus() != OrderStatus.PREPARING) {
            throw new IllegalStateException("준비 중 상태에서만 배달 준비 완료가 가능합니다.");
        }

        order.changeStatus(OrderStatus.READY_FOR_DELIVERY);

        OrderReadyForDeliveryEvent event = new OrderReadyForDeliveryEvent(
                order.getOrderId(),
                order.getId(),
                order.getStoreId(),
                order.getStoreLat(),
                order.getStoreLng(),
                order.getDeliveryAddress(),
                LocalDateTime.now()
        );

        kafkaTemplate.send(topic, String.valueOf(order.getOrderId()), event);
    }

    @Transactional
    public void cancelOrder(Long orderReceiveId) {
        OrderReceive order = orderReceiveRepository.findById(orderReceiveId)
                .orElseThrow(() -> new IllegalArgumentException("주문이 없습니다."));

        order.changeStatus(OrderStatus.CANCELED);

        OrderCanceledEvent event = new OrderCanceledEvent(
                order.getOrderId(),
                order.getId(),
                "store canceled",
                LocalDateTime.now()
        );

        kafkaTemplate.send("order.canceled", String.valueOf(order.getOrderId()), event);
    }
}

package com.delivery.order.consumer;

import com.delivery.common.event.DeliveryCompletedEvent;
import com.delivery.order.entity.Order;
import com.delivery.order.entity.OrderStatus;
import com.delivery.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DeliveryCompletedConsumer {

    private final OrderRepository orderRepository;

    @KafkaListener(topics = "delivery.completed", groupId = "order-service")
    @Transactional
    public void consume(DeliveryCompletedEvent event) {
        Order order = orderRepository.findById(event.orderId())
                .orElseThrow(() -> new IllegalArgumentException("주문이 없습니다."));

        if (order.getStatus() != OrderStatus.DELIVERY) {
            return;
        }

        order.changeStatus(OrderStatus.COMPLETED);
    }
}

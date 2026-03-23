package com.delivery.order.consumer;

import com.delivery.order.dto.*;
import com.delivery.order.entity.Order;
import com.delivery.order.entity.OrderItem;
import com.delivery.order.entity.OutboxEvent;
import com.delivery.order.entity.ProcessedEvent;
import com.delivery.order.repository.OrderItemRepository;
import com.delivery.order.repository.OrderRepository;
import com.delivery.order.repository.OutboxRepository;
import com.delivery.order.repository.ProcessedEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PaymentCompletedConsumer {

    private final ObjectMapper objectMapper;
    private final OrderRepository orderRepository;
    private final ProcessedEventRepository processedEventRepository;
    private final OrderItemRepository orderItemRepository;
    private final OutboxRepository outboxRepository;

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${topics.order-paid-completed}")
    private String topic;

    @KafkaListener(topics = "${topics.payment-completed}", groupId = "order-service")
    @Transactional
    public void consume(String message) throws Exception {
        PaymentCompletedEvent event = objectMapper.readValue(message, PaymentCompletedEvent.class);

        if (processedEventRepository.existsByEventId(event.eventId())) {
            return;
        }

        Order order = orderRepository.findById(event.orderId())
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

        if (!order.isPaid()) {
            order.markPaid();
        }

        List<OrderItem> items = orderItemRepository.findByOrderId(event.orderId());

        StoreOrderCreatedEvent storeEvent = new StoreOrderCreatedEvent(
                UUID.randomUUID().toString(),
                order.getId(),
                order.getCustomerId(),
                order.getCustomerEmail(),
                order.getStoreId(),
                order.getStoreName(),
                order.getDeliveryAddress(),
                order.getTotalAmount(),
                order.getRequestMessage(),
                order.getCreatedAt(),
                items.stream()
                        .map(item -> new StoreOrderItemDto(
                                item.getMenuId(),
                                item.getMenuName(),
                                item.getMenuPrice(),
                                item.getQuantity()
                        ))
                        .toList()
        );

        String payload = objectMapper.writeValueAsString(storeEvent);

        outboxRepository.save(new OutboxEvent(
                storeEvent.eventId(),
                "ORDER",
                String.valueOf(order.getId()),
                topic,
                payload
        ));

        processedEventRepository.save(new ProcessedEvent(event.eventId()));
    }
}
package com.delivery.order.consumer;

import com.delivery.order.dto.PaymentCompletedEvent;
import com.delivery.order.entity.Order;
import com.delivery.order.entity.ProcessedEvent;
import com.delivery.order.repository.OrderRepository;
import com.delivery.order.repository.ProcessedEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PaymentCompletedConsumer {

    private final ObjectMapper objectMapper;
    private final OrderRepository orderRepository;
    private final ProcessedEventRepository processedEventRepository;

    @KafkaListener(topics = "${topics.payment-completed}", groupId = "order-service")
    @Transactional
    public void consume(String message) throws Exception {
        PaymentCompletedEvent event = objectMapper.readValue(message, PaymentCompletedEvent.class);

        if (processedEventRepository.existsByEventId(event.eventId())) {
            return;
        }

        Order order = orderRepository.findById(event.orderId())
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

        order.markPaid();

        processedEventRepository.save(new ProcessedEvent(event.eventId()));
    }
}

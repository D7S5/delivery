package com.delivery.payment.outbox;

import com.delivery.payment.entity.OutboxEvent;
import com.delivery.payment.entity.OutboxStatus;
import com.delivery.payment.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PaymentOutboxPublisher {

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${outbox.payment-completed-topic}")
    private String topic;

    @Scheduled(fixedDelay = 1000)
    @Transactional
    public void publishPendingEvents() {
        List<OutboxEvent> events = outboxRepository.findByTop100ByStatusOrderByIdAsc(OutboxStatus.PENDING);

        for (OutboxEvent event : events) {
            kafkaTemplate.send(topic, event.getEventId(), event.getPayload());
            event.markPublished();
        }
    }
}
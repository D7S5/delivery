package com.delivery.order.publisher;

import com.delivery.order.entity.OutboxEvent;
import com.delivery.order.entity.OutboxStatus;
import com.delivery.order.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OutboxPublisher {

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;


    @Scheduled(fixedDelay = 3000)
    @Transactional
    public void publish() {
        List<OutboxEvent> events = outboxRepository.findTop100ByStatusOrderByIdAsc(OutboxStatus.INIT);

        for (OutboxEvent event : events) {
            kafkaTemplate.send(event.getTopic(), event.getAggregateId(), event.getPayload());
        }
    }
}

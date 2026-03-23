package com.delivery.store.consumer;

import com.delivery.store.dto.OrderDetailResponse;
import com.delivery.store.dto.StoreOrderCreatedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class OrderPaidConsumer {

    private final ObjectMapper objectMapper;


    @KafkaListener(topics = "store-order-created", groupId = "store-order-created")
    @Transactional
    public void consume(String message) throws Exception {
        StoreOrderCreatedEvent payload = objectMapper.readValue(message, StoreOrderCreatedEvent.class);

        System.out.println(payload);
    }
}

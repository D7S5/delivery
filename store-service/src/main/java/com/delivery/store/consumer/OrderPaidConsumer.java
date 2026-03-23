package com.delivery.store.consumer;

import com.delivery.store.dto.OrderDetailResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class OrderPaidConsumer {

    private final ObjectMapper objectMapper;


    @KafkaListener(topics = "paid.completed")
    @Transactional
    public void consume(String message) throws Exception {
        OrderDetailResponse payload = objectMapper.readValue(message, OrderDetailResponse.class);


    }
}

package com.example.riderservice.consume;

import com.delivery.common.event.OrderReadyForDeliveryEvent;
import com.example.riderservice.service.DispatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderReadyForDeliveryConsumer {

    private final DispatchService dispatchService;

    @KafkaListener(
            topics = "order.ready-for-delivery",
            groupId = "rider-service"
    )
    public void consume(OrderReadyForDeliveryEvent event) {
        System.out.println("============dispatch============");
        dispatchService.dispatch(event);
    }
}

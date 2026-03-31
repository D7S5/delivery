package com.delivery.store.consumer;

import com.delivery.store.dto.OrderDetailResponse;
import com.delivery.store.dto.StoreOrderCreatedEvent;
import com.delivery.store.dto.StoreOrderItemDto;
import com.delivery.store.entity.OrderReceive;
import com.delivery.store.entity.OrderReceiveItem;
import com.delivery.store.entity.OrderStatus;
import com.delivery.store.entity.Store;
import com.delivery.store.repository.OrderReceiveRepository;
import com.delivery.store.repository.StoreRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class OrderPaidConsumer {

    private final ObjectMapper objectMapper;
    private final StoreRepository storeRepository;
    private final OrderReceiveRepository orderReceiveRepository;

    @KafkaListener(topics = "store-order-created", groupId = "store-service")
    @Transactional
    public void consume(String message) throws Exception {
        StoreOrderCreatedEvent payload = objectMapper.readValue(message, StoreOrderCreatedEvent.class);

        Store store = storeRepository.findById(payload.storeId())
                .orElseThrow(() -> new IllegalArgumentException("가게를 찾을 수 없습니다."));

        OrderReceive orderReceive = OrderReceive.builder()
                .orderId(payload.orderId())
                .customerId(payload.customerId())
                .customerEmail(payload.customerEmail())
                .storeId(payload.storeId())
                .storeName(payload.storeName())
                .deliveryAddress(payload.deliveryAddress())
                .storeLat(store.getStoreLat())
                .storeLng(store.getStoreLng())
                .status(OrderStatus.RECEIVE_ORDER)
                .totalAmount(payload.totalAmount())
                .requestMessage(payload.requestMessage())
                .createdAt(payload.createdAt())
                .build();

        for (StoreOrderItemDto itemDto : payload.items()) {
            OrderReceiveItem item = OrderReceiveItem.builder()
                    .menuId(itemDto.menuId())
                    .menuName(itemDto.menuName())
                    .menuPrice(itemDto.menuPrice())
                    .quantity(itemDto.quantity())
                    .build();

            orderReceive.addItem(item);
        }
        orderReceiveRepository.save(orderReceive);
    }
}

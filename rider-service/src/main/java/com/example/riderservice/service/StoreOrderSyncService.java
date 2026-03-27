package com.example.riderservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Service
@RequiredArgsConstructor
public class StoreOrderSyncService {

    private final StoreOrderClient storeOrderClient;

    public void startDelivery(Long orderReceiveId) {
        storeOrderClient.startDelivery(orderReceiveId);
    }

    public void completeDelivery(Long orderReceiveId) {
        storeOrderClient.completeDelivery(orderReceiveId);
    }

    @FeignClient(name = "store-service", url = "${store-service.url}")
    interface StoreOrderClient {

        @PatchMapping("/internal/store/orders/{orderReceiveId}/delivery")
        void startDelivery(@PathVariable("orderReceiveId") Long orderReceiveId);

        @PatchMapping("/internal/store/orders/{orderReceiveId}/complete")
        void completeDelivery(@PathVariable("orderReceiveId") Long orderReceiveId);
    }
}

package com.example.riderservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "store-service", url = "${store-service.url}")
public interface StoreOrderClient {

    @PutMapping("/internal/store/orders/{orderReceiveId}/delivery")
    void startDelivery(@PathVariable("orderReceiveId") Long orderReceiveId);

    @PutMapping("/internal/store/orders/{orderReceiveId}/complete")
    void completeDelivery(@PathVariable("orderReceiveId") Long orderReceiveId);
}
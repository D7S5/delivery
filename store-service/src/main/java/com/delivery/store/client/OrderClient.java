package com.delivery.store.client;

import com.delivery.common.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "order-service", url ="${order-service.url}")
public interface OrderClient {
    @PutMapping("/internal/orders/{orderId}/prepared")
    ApiResponse<Void> prepared(@PathVariable("orderId") Long orderId);

    @PutMapping("/internal/orders/{orderId}/delivery")
    ApiResponse<Void> delivery(@PathVariable("orderId") Long orderId);

    @PutMapping("/internal/orders/{orderId}/complete")
    ApiResponse<Void> complete(@PathVariable("orderId") Long orderId);

    @PutMapping("/internal/orders/{orderId}/cancel")
    ApiResponse<Void> cancel(@PathVariable("orderId") Long orderId);
}
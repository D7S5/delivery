package com.delivery.store.client;

import com.delivery.common.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "order-service", url = "${order-service.url}")
public interface OrderClient {

    @PatchMapping("/internal/orders/{orderId}/prepared")
    ApiResponse<Void> prepared(@PathVariable("orderId") Long orderId);

    @PatchMapping("/internal/orders/{orderId}/ready")
    ApiResponse<Void> ready(@PathVariable("orderId") Long orderId);

    @PatchMapping("/internal/orders/{orderId}/delivery")
    ApiResponse<Void> delivery(@PathVariable("orderId") Long orderId);

    @PatchMapping("/internal/orders/{orderId}/complete")
    ApiResponse<Void> complete(@PathVariable("orderId") Long orderId);

    @PatchMapping("/internal/orders/{orderId}/cancel")
    ApiResponse<Void> cancel(@PathVariable("orderId") Long orderId);
}
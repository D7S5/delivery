package com.example.riderservice.client;

import com.delivery.common.ApiResponse;
import com.example.riderservice.dto.OrderStatusResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "order-service", url = "${order-service.url}")
public interface OrderServiceClient {

    @GetMapping("/internal/orders/{orderId}/status")
    Map<String, String> getOrderStatus(@PathVariable("orderId") Long orderId);

    @PatchMapping("/internal/orders/{orderId}/delivery")
    ApiResponse<Void> markDelivery(@PathVariable("orderId") Long orderId);

    @PatchMapping("/internal/orders/{orderId}/complete")
    ApiResponse<Void> markComplete(@PathVariable("orderId") Long orderId);
}
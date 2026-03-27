package com.example.riderservice.client;

import com.example.riderservice.dto.OrderStatusResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "order-service", url = "${order-service.url}")
public interface OrderServiceClient {
    @GetMapping("/internal/orders/{orderId}/status")
    OrderStatusResponse getOrderStatus(@PathVariable Long orderId);
}

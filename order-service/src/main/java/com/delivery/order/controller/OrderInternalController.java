package com.delivery.order.controller;

import com.delivery.common.ApiResponse;
import com.delivery.order.service.OrderInternalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/internal/orders")
@RequiredArgsConstructor
public class OrderInternalController {

    private final OrderInternalService service;

    @PatchMapping("/{orderId}/prepared")
    public ApiResponse<Void> prepared(@PathVariable Long orderId) {
        return service.prepared(orderId);
    }

    @PatchMapping("/{orderId}/ready")
    public ApiResponse<Void> ready(@PathVariable Long orderId) {
        return service.ready(orderId);
    }

    @PatchMapping("/{orderId}/delivery")
    public ApiResponse<Void> delivery(@PathVariable Long orderId) {
        return service.delivery(orderId);
    }

    @PatchMapping("/{orderId}/complete")
    public ApiResponse<Void> orderComplete(@PathVariable Long orderId) {
        return service.complete(orderId);
    }

    @PatchMapping("/{orderId}/cancel")
    public ApiResponse<Void> cancel(@PathVariable Long orderId) {
        return service.cancel(orderId);
    }

    @GetMapping("/{orderId}/status")
    public Map<String, String> getStatus(@PathVariable Long orderId) {
        return service.getStatus(orderId);
    }
}
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

    @PutMapping("/{orderId}/prepared")
    public ApiResponse<Void> prepared(@PathVariable Long orderId) {
        System.out.println("internal prepared orderId = " + orderId);
        return service.prepared(orderId);
    }

    @PutMapping("/{orderId}/ready")
    public ApiResponse<Void> ready(@PathVariable Long orderId) {
        System.out.println("============== ready ================");
        return service.ready(orderId);
    }

    @PutMapping("/{orderId}/delivery")
    public ApiResponse<Void> delivery(@PathVariable Long orderId) {
        return service.delivery(orderId);
    }
    @PutMapping("/{orderId}/complete")
    public ApiResponse<Void> orderComplete(@PathVariable Long orderId) {
        return service.complete(orderId);
    }

    @PutMapping("/{orderId}/cancel")
    public ApiResponse<Void> cancel(@PathVariable Long orderId) {
        return service.cancel(orderId);
    }

    @GetMapping("/{orderId}/status")
    public Map<String, String> getStatus(@PathVariable Long orderId) {
        return service.getStatus(orderId);
    }
}
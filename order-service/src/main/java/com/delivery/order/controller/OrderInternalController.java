package com.delivery.order.controller;

import com.delivery.common.ApiResponse;
import com.delivery.order.service.OrderInternalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/orders/")
@RequiredArgsConstructor
public class OrderInternalController {

    private final OrderInternalService service;

    @PutMapping("/{orderId}/prepared")
    public ApiResponse<Void> prepared(@PathVariable Long orderId) {
        System.out.println("orderId = " + orderId);
        return service.prepared(orderId);
    }

    @PutMapping("/{orderId}/delivery")
    public ApiResponse<Void> delivery(@PathVariable Long orderId) {
        System.out.println("orderId = " + orderId);
        return service.delivery(orderId);
    }

    @PutMapping("/{orderId}/complete")
    public ApiResponse<Void> orderComplete(@PathVariable Long orderId) {
        System.out.println("orderId = " + orderId);
        return service.complete(orderId);
    }

    @PutMapping("/{orderId}/cancel")
    public ApiResponse<Void> orderCancel(@PathVariable Long orderId) {
        System.out.println("orderId = " + orderId);
        return service.cancel(orderId);
    }
}

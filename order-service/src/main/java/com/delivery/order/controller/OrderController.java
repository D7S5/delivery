package com.delivery.order.controller;

import com.delivery.common.ApiResponse;
import com.delivery.order.dto.CreateOrderRequest;
import com.delivery.order.dto.OrderDetailResponse;
import com.delivery.order.dto.OrderSummaryResponse;
import com.delivery.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ApiResponse<OrderDetailResponse> createOrder(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Email") String email,
            @RequestHeader("X-User-Role") String role,
            @Valid @RequestBody CreateOrderRequest request
    ) {
        return orderService.createOrder(userId, email, role, request);
    }

    @GetMapping("/my")
    public ApiResponse<List<OrderSummaryResponse>> getMyOrders(
            @RequestHeader("X-User-Id") Long userId
    ) {
        return orderService.getMyOrders(userId);
    }

    @GetMapping("/{orderId}")
    public ApiResponse<OrderDetailResponse> getMyOrderDetail(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long orderId
    ) {
        return orderService.getMyOrderDetail(userId, orderId);
    }
}
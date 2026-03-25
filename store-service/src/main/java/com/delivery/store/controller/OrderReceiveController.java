package com.delivery.store.controller;

import com.delivery.common.ApiResponse;
import com.delivery.store.dto.OrderReceiveDetailResponse;
import com.delivery.store.dto.OrderReceiveSummaryResponse;
import com.delivery.store.entity.Store;
import com.delivery.store.service.OrderReceiveService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/store/orders")
@RequiredArgsConstructor
public class OrderReceiveController {

    private final OrderReceiveService orderReceiveService;

    @GetMapping
    public ApiResponse<List<OrderReceiveSummaryResponse>> getMyStoreOrders(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String role
    ) {
        System.out.println("X-User-Id = " + userId);
        System.out.println("X-User-Role = " + role);
        return orderReceiveService.getMyStoreOrders(userId, role);
    }

    @GetMapping("/{orderReceiveId}")
    public ApiResponse<OrderReceiveDetailResponse> getMyStoreOrderDetail(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String role,
            @PathVariable Long orderReceiveId
    ) {
        System.out.println("X-User-Id = " + userId);
        System.out.println("X-User-Role = " + role);
        return orderReceiveService.getMyStoreOrderDetail(userId, role, orderReceiveId);
    }

    @PatchMapping("/{orderReceiveId}/preparing")
    public ApiResponse<Void> startPreparing(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String role,
            @PathVariable Long orderReceiveId
    ) {
        System.out.println("X-User-Id = " + userId);
        System.out.println("X-User-Role = " + role);
        return orderReceiveService.startPreparing(userId, role, orderReceiveId);
    }

    @PatchMapping("/{orderReceiveId}/delivery")
    public ApiResponse<Void> startDelivery(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String role,
            @PathVariable Long orderReceiveId
    ) {
        return orderReceiveService.startDelivery(userId, role, orderReceiveId);
    }

    @PatchMapping("/{orderReceiveId}/complete")
    public ApiResponse<Void> complete(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String role,
            @PathVariable Long orderReceiveId
    ) {
        return orderReceiveService.completeOrder(userId, role, orderReceiveId);
    }

    @PatchMapping("/{orderReceiveId}/cancel")
    public ApiResponse<Void> cancelOrder(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String role,
            @PathVariable Long orderReceiveId
    ) {
        return orderReceiveService.cancelOrder(userId, role, orderReceiveId);
    }
}



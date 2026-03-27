package com.delivery.store.controller;

import com.delivery.common.ApiResponse;
import com.delivery.store.service.OrderReceiveService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/store/orders")
@RequiredArgsConstructor
public class StoreOrderInternalController {

    private final OrderReceiveService orderReceiveService;

    @PatchMapping("/{orderReceiveId}/delivery")
    public ApiResponse<Void> startDelivery(@PathVariable Long orderReceiveId) {
        return orderReceiveService.startDeliveryByRider(orderReceiveId);
    }

    @PatchMapping("/{orderReceiveId}/complete")
    public ApiResponse<Void> complete(@PathVariable Long orderReceiveId) {
        return orderReceiveService.completeOrderByRider(orderReceiveId);
    }
}
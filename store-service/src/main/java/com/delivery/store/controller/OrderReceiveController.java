package com.delivery.store.controller;

import com.delivery.common.ApiResponse;
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

//    @GetMapping
//    public ApiResponse<List<Store>>

    @PatchMapping("/{orderReceiveId}/preparing")
    public ApiResponse<Void> startPreparing(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String role,
            @PathVariable Long orderReceiveId
    ) {
        return orderReceiveService.startPreparing(userId, role, orderReceiveId);
    }
}

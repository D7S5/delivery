package com.delivery.store.controller;

import com.delivery.common.ApiResponse;
import com.delivery.store.dto.OrderReceiveDetailResponse;
import com.delivery.store.entity.OrderReceive;
import com.delivery.store.repository.OrderReceiveRepository;
import com.delivery.store.service.OrderReceiveService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/store/orders")
@RequiredArgsConstructor
public class OrderReceiveInternalController {

    private final OrderReceiveRepository orderReceiveRepository;
    private final OrderReceiveService orderReceiveService;

    @GetMapping("/{orderReceiveId}")
    public ApiResponse<OrderReceiveDetailResponse> getOrder(
            @PathVariable Long orderReceiveId
    ) {
        OrderReceive orderReceive = orderReceiveRepository.findById(orderReceiveId)
                .orElseThrow(() -> new IllegalArgumentException("주문이 존재하지 않습니다."));
        return new ApiResponse<>(true, OrderReceiveDetailResponse.from(orderReceive), "주문 조회 성공");
    }

    @PutMapping("/{orderReceiveId}/delivery")
    public ApiResponse<Void> startDelivery(
            @PathVariable Long orderReceiveId
    ) {
        return orderReceiveService.startDeliveryByRider(orderReceiveId);
    }

    @PutMapping("/{orderReceiveId}/complete")
    public ApiResponse<Void> completeDelivery(
            @PathVariable Long orderReceiveId
    ) {
        return orderReceiveService.completeOrderByRider(orderReceiveId);
    }
}
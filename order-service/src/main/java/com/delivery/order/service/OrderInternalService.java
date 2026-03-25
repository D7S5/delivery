package com.delivery.order.service;

import com.delivery.common.ApiResponse;
import com.delivery.order.entity.Order;
import com.delivery.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderInternalService {

    private final OrderRepository orderRepository;

    @Transactional
    public ApiResponse<Void> prepared(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException(""));

//        Long customerId = order.getCustomerId();

        order.prepared();

        return new ApiResponse<>(true, null, "주문 중 상태로 변경되었습니다.");
    }

    @Transactional
    public ApiResponse<Void> delivery(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException(""));

        order.delivery();

        return new ApiResponse<>(true, null, "주문 중 상태로 변경되었습니다.");
    }

    @Transactional
    public ApiResponse<Void> complete(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException(""));

        order.complete();

        return new ApiResponse<>(true, null, "주문 중 상태로 변경되었습니다.");
    }

    @Transactional
    public ApiResponse<Void> cancel(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException(""));

        order.cancel();

        return new ApiResponse<>(true, null, "주문 중 상태로 변경되었습니다.");
    }
}

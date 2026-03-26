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
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

//        Long customerId = order.getCustomerId();

        order.prepared();

        return new ApiResponse<>(true, null, "준비중 상태로 변경되었습니다.");
    }

    @Transactional
    public ApiResponse<Void> delivery(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

        order.delivery();

        return new ApiResponse<>(true, null, "배달 중 상태로 변경되었습니다.");
    }

    @Transactional
    public ApiResponse<Void> complete(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

        order.complete();

        return new ApiResponse<>(true, null, "배달이 완료되었습니다.");
    }

    @Transactional
    public ApiResponse<Void> cancel(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

        order.cancel();

        return new ApiResponse<>(true, null, "주문이 취소되었습니다..");
    }
}

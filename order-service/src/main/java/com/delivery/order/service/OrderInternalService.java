package com.delivery.order.service;

import com.delivery.common.ApiResponse;
import com.delivery.order.dto.GetStatus;
import com.delivery.order.entity.Order;
import com.delivery.order.entity.OrderStatus;
import com.delivery.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.shaded.com.google.protobuf.Api;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderInternalService {

    private final OrderRepository orderRepository;

    @Transactional
    public ApiResponse<Void> prepared(Long orderId) {
        Order order = getOrder(orderId);
        order.prepared();
        return new ApiResponse<>(true, null, "주문이 준비중 상태로 변경되었습니다.");
    }

    @Transactional
    public ApiResponse<Void> ready(Long orderId) {
        Order order = getOrder(orderId);
        System.out.println(order.getStatus());
        if (order.getStatus() != OrderStatus.PREPARING) {
            throw new IllegalArgumentException("준비중 상태에서만 배차 대기 상태로 변경할 수 있습니다.");
        }

        order.changeStatus(OrderStatus.READY_FOR_DELIVERY);
        System.out.println("ready ===> READY_FOR_DELIVERY " + order.getStatus());
        return new ApiResponse<>(true, null, "주문이 배차 대기 상태로 변경되었습니다.");
    }

    @Transactional
    public ApiResponse<Void> delivery(Long orderId) {
        Order order = getOrder(orderId);

        if (order.getStatus() != OrderStatus.READY_FOR_DELIVERY) {
            throw new IllegalArgumentException("배차 대기 상태에서만 배달 중으로 변경할 수 있습니다.");
        }

        order.changeStatus(OrderStatus.DELIVERY);
        return new ApiResponse<>(true, null, "주문이 배달 중 상태로 변경되었습니다.");
    }

    @Transactional
    public ApiResponse<Void> complete(Long orderId) {
        Order order = getOrder(orderId);
        order.complete();
        return new ApiResponse<>(true, null, "주문이 완료 상태로 변경되었습니다.");
    }

    @Transactional
    public ApiResponse<Void> cancel(Long orderId) {
        Order order = getOrder(orderId);
        order.cancel();
        return new ApiResponse<>(true, null, "주문이 취소되었습니다.");
    }

    @Transactional(readOnly = true)
    public ApiResponse<GetStatus> getStatus(Long orderId) {
        Order order = getOrder(orderId);
        GetStatus response = new GetStatus(order.getStatus().name());
        return new ApiResponse<>(true, response, "상태 응답");
    }

    private Order getOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));
    }
}
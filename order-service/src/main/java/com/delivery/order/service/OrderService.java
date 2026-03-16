package com.delivery.order.service;

import com.delivery.common.ApiResponse;
import com.delivery.order.dto.CreateOrderRequest;
import com.delivery.order.dto.OrderDetailResponse;
import com.delivery.order.dto.OrderItemResponse;
import com.delivery.order.dto.OrderSummaryResponse;
import com.delivery.order.entity.Order;
import com.delivery.order.entity.OrderItem;
import com.delivery.order.entity.OrderStatus;
import com.delivery.order.repository.OrderItemRepository;
import com.delivery.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;



    @Transactional
    public ApiResponse<OrderDetailResponse> createOrder(Long customerId, String customerEmail, String role, CreateOrderRequest request) {
        validateCustomer(role);

        int totalAmount = request.items().stream()
                .mapToInt(item -> item.menuPrice() * item.quantity())
                .sum();

        Order order = Order.builder()
                .customerId(customerId)
                .customerEmail(customerEmail)
                .storeId(request.storeId())
                .storeName(request.storeName())
                .deliveryAddress(request.deliveryAddress())
                .totalAmount(totalAmount)
                .status(OrderStatus.CREATED)
                .requestMessage(request.requestMessage())
                .createdAt(LocalDateTime.now())
                .build();

        Order savedOrder = orderRepository.save(order);

        List<OrderItem> savedItems = request.items().stream()
                .map(item -> OrderItem.builder()
                        .orderId(savedOrder.getId())
                        .menuId(item.menuId())
                        .menuName(item.menuName())
                        .menuPrice(item.menuPrice())
                        .quantity(item.quantity())
                        .itemTotalPrice(item.menuPrice() * item.quantity())
                        .build())
                .map(orderItemRepository::save)
                .toList();

        OrderDetailResponse response = new OrderDetailResponse(
                savedOrder.getId(),
                savedOrder.getCustomerId(),
                savedOrder.getCustomerEmail(),
                savedOrder.getStoreId(),
                savedOrder.getStoreName(),
                savedOrder.getDeliveryAddress(),
                savedOrder.getTotalAmount(),
                savedOrder.getStatus(),
                savedOrder.getRequestMessage(),
                savedOrder.getCreatedAt(),
                savedItems.stream().map(OrderItemResponse::from).toList()
        );

        return new ApiResponse<>(true, response, "주문 생성이 완료되었습니다.");
    }

    public ApiResponse<List<OrderSummaryResponse>> getMyOrders(Long customerId) {
        List<OrderSummaryResponse> orders = orderRepository.findByCustomerIdOOrderByIdDesc(customerId)
                .stream()
                .map(OrderSummaryResponse::from)
                .toList();

        return new ApiResponse<>(true, orders, "내 주문 조회 성공");
    }




    public void validateCustomer(String role) {
        if (!"CUSTOMER".equals(role)) {
            throw new IllegalArgumentException("고객 권한만 주문이 가능합니다.");
        }
    }
}


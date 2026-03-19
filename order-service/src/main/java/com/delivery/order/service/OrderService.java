package com.delivery.order.service;

import com.delivery.common.ApiResponse;
import com.delivery.order.client.StoreClient;
import com.delivery.order.client.dto.OrderInternalResponse;
import com.delivery.order.client.dto.StoreInternalResponse;
import com.delivery.order.client.dto.StoreStatus;
import com.delivery.order.dto.*;
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
    private final StoreClient storeClient;

    @Transactional
    public ApiResponse<OrderDetailResponse> createOrder(Long customerId, String customerEmail, String role, CreateOrderRequest request) {
        validateCustomer(role);

        System.out.println("createOrder");

        ApiResponse<StoreInternalResponse> storeResponse = storeClient.getInternalStore(request.storeId());

        if (storeResponse == null || !storeResponse.isSuccess() || storeResponse.getData() == null) {
            throw new IllegalArgumentException("가게 정보를 조회할 수 없습니다.");
        }

        System.out.println("storeResponse = " + storeResponse);
        StoreInternalResponse store = storeResponse.getData();

        if (store.status() != StoreStatus.OPEN) {
            throw new IllegalArgumentException("현재 영업 중인 가게가 아닙니다.");
        }

        int totalAmount = request.items().stream()
                .mapToInt(item -> item.menuPrice() * item.quantity())
                .sum();

        if (totalAmount < store.minOrderAmount()) {
            throw new IllegalArgumentException("최소 주문 가능 금액은 " + store.minOrderAmount() + "입니다.");
        }

        System.out.println("totalAmount = " + totalAmount);
        System.out.println("minOrderAmount = " + store.minOrderAmount());

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
        List<OrderSummaryResponse> orders = orderRepository.findByCustomerIdOrderByIdDesc(customerId)
                .stream()
                .map(OrderSummaryResponse::from)
                .toList();

        return new ApiResponse<>(true, orders, "내 주문 조회 성공");
    }
    public ApiResponse<OrderDetailResponse> getMyOrderDetail(Long customerId, Long orderId) {
        Order order = orderRepository.findByIdAndCustomerId(orderId, customerId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);

        return new ApiResponse<>(
                true,
                toOrderDetailResponse(order, items),
                "주문 상세 조회 성공"
        );
    }

    public ApiResponse<OrderInternalResponse> getInternalOrder(Long orderId) {
        Order order = getOrder(orderId);
        return new ApiResponse<>(true, OrderInternalResponse.from(order), "주문 내부 조회 성공");
    }
    @Transactional
    public ApiResponse<Void> markOrderPaid(Long orderId) {
        Order order = getOrder(orderId);
        order.markPaid();
        return new ApiResponse<>(true, null, "주문 상태가 결제 완료 상태로 변경되었습니다.");
    }

    @Transactional
    public ApiResponse<Void> cancelOrder(Long customerId, String role, Long orderId) {
        validateCustomer(role);

        Order order = orderRepository.findByIdAndCustomerId(orderId, customerId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

        order.cancel();
        return new ApiResponse<>(true, null, "주문 취소가 완료되었습니다.");
    }

    private Order getOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));
    }

    private OrderDetailResponse toOrderDetailResponse(Order order, List<OrderItem> items) {
        return new OrderDetailResponse(
                order.getId(),
                order.getCustomerId(),
                order.getCustomerEmail(),
                order.getStoreId(),
                order.getStoreName(),
                order.getDeliveryAddress(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getRequestMessage(),
                order.getCreatedAt(),
                items.stream().map(OrderItemResponse::from).toList()
        );
    }

    public void validateCustomer(String role) {
        if (!"CUSTOMER".equals(role)) {
            throw new IllegalArgumentException("고객 권한만 주문이 가능합니다.");
        }
    }
}
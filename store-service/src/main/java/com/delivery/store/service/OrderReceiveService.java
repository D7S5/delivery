package com.delivery.store.service;

import com.delivery.common.ApiResponse;
import com.delivery.store.client.OrderClient;
import com.delivery.store.dto.OrderReceiveDetailResponse;
import com.delivery.store.dto.OrderReceiveSummaryResponse;
import com.delivery.store.entity.OrderReceive;
import com.delivery.store.entity.Store;
import com.delivery.store.producer.OrderReadyForDeliveryProducer;
import com.delivery.store.repository.OrderReceiveRepository;
import com.delivery.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderReceiveService {

    private final OrderReceiveRepository orderReceiveRepository;
    private final StoreRepository storeRepository;
    private final OrderClient orderClient;
    private final OrderReadyForDeliveryProducer orderReadyForDeliveryProducer;

    @Transactional(readOnly = true)
    public ApiResponse<List<OrderReceiveSummaryResponse>> getMyStoreOrders(Long userId, String role) {
        validateOwner(role);

        List<Store> stores = storeRepository.findAllByOwnerId(userId);

        if (stores.isEmpty()) {
            throw new IllegalArgumentException("내 가게 정보를 찾을 수 없습니다.");
        }

        List<Long> storeIds = stores.stream()
                .map(Store::getId)
                .toList();

        List<OrderReceiveSummaryResponse> responses = orderReceiveRepository
                .findAllByStoreIdInOrderByIdDesc(storeIds)
                .stream()
                .map(OrderReceiveSummaryResponse::from)
                .toList();

        return new ApiResponse<>(true, responses, "가게 주문 목록 조회 성공");
    }

    @Transactional(readOnly = true)
    public ApiResponse<OrderReceiveDetailResponse> getMyStoreOrderDetail(Long userId, String role, Long orderReceiveId) {
        validateOwner(role);

        Store store = storeRepository.findByOwnerId(userId)
                .orElseThrow(() -> new IllegalArgumentException("내 가게 정보를 찾을 수 없습니다."));

        OrderReceive orderReceive = orderReceiveRepository.findByIdAndStoreId(orderReceiveId, store.getId())
                .orElseThrow(() -> new IllegalArgumentException("내 가게 주문이 아니거나 주문이 존재하지 않습니다."));

        return new ApiResponse<>(true, OrderReceiveDetailResponse.from(orderReceive), "가게 주문 상세 조회 성공");
    }

    @Transactional
    public ApiResponse<Void> startPreparing(Long userId, String role, Long orderReceiveId) {
        validateOwner(role);

        OrderReceive orderReceive = getMyStoreOrder(userId, orderReceiveId);

        orderReceive.startPreparing();
        orderClient.prepared(orderReceive.getOrderId());

        return new ApiResponse<>(true, null, "주문이 준비중으로 변경되었습니다.");
    }

    @Transactional
    public ApiResponse<Void> markReadyForDelivery(Long userId, String role, Long orderReceiveId) {
        validateOwner(role);

        OrderReceive orderReceive = getMyStoreOrder(userId, orderReceiveId);
        orderReceive.markReadyForDelivery();

        try {
            orderClient.ready(orderReceive.getOrderId());
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        try {
            orderReadyForDeliveryProducer.publish(orderReceive);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        return new ApiResponse<>(true, null, "준비완료 및 라이더 배차 요청이 처리되었습니다.");
    }
    @Transactional
    public ApiResponse<Void> startDeliveryByRider(Long orderReceiveId) {
        OrderReceive orderReceive = orderReceiveRepository.findById(orderReceiveId)
                .orElseThrow(() -> new IllegalArgumentException("주문이 존재하지 않습니다."));

        orderReceive.startDelivery();

        return new ApiResponse<>(true, null, "배달 상태로 변경되었습니다.");
    }

    @Transactional
    public ApiResponse<Void> completeOrderByRider(Long orderReceiveId) {
        OrderReceive orderReceive = orderReceiveRepository.findById(orderReceiveId)
                .orElseThrow(() -> new IllegalArgumentException("주문이 존재하지 않습니다."));

        orderReceive.complete();

        return new ApiResponse<>(true, null, "주문 완료 처리되었습니다.");
    }

    @Transactional
    public ApiResponse<Void> cancelOrder(Long userId, String role, Long orderReceiveId) {
        validateOwner(role);

        OrderReceive orderReceive = getMyStoreOrder(userId, orderReceiveId);
        orderReceive.cancel();
        orderClient.cancel(orderReceive.getOrderId());

        return new ApiResponse<>(true, null, "주문이 취소되었습니다.");
    }

    private void validateOwner(String role) {
        if (!"OWNER".equals(role)) {
            throw new IllegalArgumentException("점주만 접근할 수 있습니다.");
        }
    }

    private OrderReceive getMyStoreOrder(Long userId, Long orderReceiveId) {
        List<Store> stores = storeRepository.findAllByOwnerId(userId);

        if (stores.isEmpty()) {
            throw new IllegalArgumentException("내 가게 정보를 찾을 수 없습니다.");
        }

        List<Long> storeIds = stores.stream()
                .map(Store::getId)
                .toList();

        OrderReceive response = orderReceiveRepository.findByIdAndStoreIdIn(orderReceiveId, storeIds)
                .orElseThrow(() -> new IllegalArgumentException("내 가게 주문이 아니거나 주문이 존재하지 않습니다."));
        return response;
    }
}
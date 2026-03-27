package com.delivery.store.service;

import com.delivery.common.ApiResponse;
import com.delivery.store.client.OrderClient;
import com.delivery.store.dto.OrderReceiveDetailResponse;
import com.delivery.store.dto.OrderReceiveSummaryResponse;
import com.delivery.store.entity.OrderReceive;
import com.delivery.store.entity.Store;
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

    @Transactional(readOnly = true)
    public ApiResponse<List<OrderReceiveSummaryResponse>> getMyStoreOrders(Long userId, String role) {
        validateOwner(role);

        Store store = storeRepository.findByOwnerId(userId)
                .orElseThrow(() -> new IllegalArgumentException("내 가게 정보를 찾을 수 없습니다."));

        List<OrderReceiveSummaryResponse> responses =
                orderReceiveRepository.findAllByStoreIdOrderByIdDesc(store.getId())
                        .stream()
                        .map(OrderReceiveSummaryResponse::from)
                        .toList();

        return new ApiResponse<>(true, responses, "가게 목록");
    }

    @Transactional(readOnly = true)
    public ApiResponse<OrderReceiveDetailResponse> getMyStoreOrderDetail(Long userId, String role, Long orderReceiveId) {
        validateOwner(role);

        Store store = storeRepository.findByOwnerId(userId)
                .orElseThrow(() -> new IllegalArgumentException("내 가게 정보를 찾을 수 없습니다."));

        OrderReceive orderReceive = orderReceiveRepository.findByIdAndStoreId(orderReceiveId, store.getId())
                .orElseThrow(() -> new IllegalArgumentException("내 가게 주문이 아니거나 주문이 존재하지 않습니다."));

        return new ApiResponse<>(true, OrderReceiveDetailResponse.from(orderReceive), "가게 세부정보");
    }

    private void validateOwner(String role) {
        if (!"OWNER".equals(role)) {
            throw new IllegalArgumentException("점주만 접근할 수 있습니다.");
        }
    }

    @Transactional
    public ApiResponse<Void> startPreparing(Long userId, String role, Long orderReceiveId) {
        if (!"OWNER".equals(role)) {
            throw new IllegalArgumentException("점주만 주문을 준비상태로 변경할 수 있습니다.");
        }
        Store store = storeRepository.findByOwnerId(userId)
                .orElseThrow(() -> new IllegalArgumentException("내 가게 정보를 찾을 수 없습니다."));

        OrderReceive orderReceive = orderReceiveRepository.findByIdAndStoreId(orderReceiveId, store.getId())
                .orElseThrow(() -> new IllegalArgumentException("내 가게 주문이 아니거나 주문이 존재하지 않습니다."));

        orderReceive.startPreparing();
        orderClient.prepared(orderReceive.getOrderId());

        return new ApiResponse<>(true, null, "주문이 준비중으로 변경되었습니다.");
    }

    @Transactional
    public ApiResponse<Void> markReadyForDelivery(Long userId, String role, Long orderReceiveId) {
        if (!"OWNER".equals(role)) {
            throw new IllegalArgumentException("점주만 주문을 배달 상태로 변경할 수 있습니다.");
        }

        Store store = storeRepository.findByOwnerId(userId)
                .orElseThrow(() -> new IllegalArgumentException("내 가게 정보를 찾을 수 없습니다."));

        OrderReceive orderReceive = orderReceiveRepository.findByIdAndStoreId(orderReceiveId, store.getId())
                .orElseThrow(() -> new IllegalArgumentException("내 가게 주문이 아니거나 주문이 존재하지 않습니다."));

        orderReceive.markReadyForDelivery();

        return new ApiResponse<>(true, null, "준비완료 상태로 변경되었습니다.");
    }

    @Transactional
    public ApiResponse<Void> completeOrder(Long userId, String role, Long orderReceiveId) {
        if (!"RIDER".equals(role)) {
            throw new IllegalArgumentException("라이더 배달확인 변경 예정");
            // 라이더
        }
        Store store = storeRepository.findByOwnerId(userId)
                .orElseThrow(() -> new IllegalArgumentException("내 가게 정보를 찾을 수 없습니다."));

        OrderReceive orderReceive = orderReceiveRepository.findByIdAndStoreId(orderReceiveId, store.getId())
                .orElseThrow(() -> new IllegalArgumentException("내 가게 주문이 아니거나 주문이 존재하지 않습니다."));

        orderReceive.complete();
        orderClient.complete(orderReceive.getOrderId());

        return new ApiResponse<>(true, null, "배달이 완료되었습니다.");
    }

    @Transactional
    public ApiResponse<Void> cancelOrder(Long userId, String role, Long orderReceiveId) {
        if (!"OWNER".equals(role)) {
            throw new IllegalArgumentException("점주만 주문을 취소상태로 변경할 수 있습니다.");
        }
        Store store = storeRepository.findByOwnerId(userId)
                .orElseThrow(() -> new IllegalArgumentException("내 가게 정보를 찾을 수 없습니다."));

        OrderReceive orderReceive = orderReceiveRepository.findByIdAndStoreId(orderReceiveId, store.getId())
                .orElseThrow(() -> new IllegalArgumentException("내 가게 주문이 아니거나 주문이 존재하지 않습니다."));

        orderReceive.cancel();
        orderClient.cancel(orderReceive.getOrderId());
        return new ApiResponse<>(true, null, "주문이 취소되었습니다.");
    }
}
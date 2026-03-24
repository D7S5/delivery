package com.delivery.store.service;

import com.delivery.common.ApiResponse;
import com.delivery.store.entity.OrderReceive;
import com.delivery.store.entity.Store;
import com.delivery.store.repository.OrderReceiveRepository;
import com.delivery.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderReceiveService {

    private final OrderReceiveRepository orderReceiveRepository;
    private final StoreRepository storeRepository;

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

        return new ApiResponse<>(true, null, "주문이 준비중으로 변경되었습니다.");
    }

    @Transactional
    public void startDelivery(Long loginStoreId, Long orderReceiveId) {
        OrderReceive orderReceive = orderReceiveRepository.findByIdAndStoreId(orderReceiveId, loginStoreId)
                .orElseThrow(() -> new IllegalArgumentException("내 가게 주문이 아니거나 주문이 존재하지 않습니다."));

        orderReceive.startDelivery();
    }

    @Transactional
    public void completeOrder(Long loginStoreId, Long orderReceiveId) {
        OrderReceive orderReceive = orderReceiveRepository.findByIdAndStoreId(orderReceiveId, loginStoreId)
                .orElseThrow(() -> new IllegalArgumentException("내 가게 주문이 아니거나 주문이 존재하지 않습니다."));

        orderReceive.complete();
    }

    @Transactional
    public void cancelOrder(Long loginStoreId, Long orderReceiveId) {
        OrderReceive orderReceive = orderReceiveRepository.findByIdAndStoreId(orderReceiveId, loginStoreId)
                .orElseThrow(() -> new IllegalArgumentException("내 가게 주문이 아니거나 주문이 존재하지 않습니다."));

        orderReceive.cancel();
    }
}

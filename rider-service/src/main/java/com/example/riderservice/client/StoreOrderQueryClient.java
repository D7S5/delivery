package com.example.riderservice.client;

import com.delivery.common.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "store-order-query-client", url = "${store-service.url}")
public interface StoreOrderQueryClient {

    @GetMapping("/internal/store/orders/{orderReceiveId}")
    ApiResponse<Map<String, Object>> getOrderResponse(@PathVariable("orderReceiveId") Long orderReceiveId);

    default Map<String, Object> getOrder(Long orderReceiveId) {
        ApiResponse<Map<String, Object>> response = getOrderResponse(orderReceiveId);
        if (response == null || response.getData() == null) {
            throw new IllegalStateException("가게 주문 정보를 불러오지 못했습니다.");
        }
        return response.getData();
    }
}
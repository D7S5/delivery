package com.delivery.payment.client;

import com.delivery.common.ApiResponse;
import com.delivery.payment.dto.OrderInternalResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class OrderClient {

    private final RestClient.Builder restClientBuilder;

    @Value("${order-service.base-url}")
    private String orderServiceBaseUrl;

    @Value("${internal.service-token}")
    private String internalServiceToken;

    public OrderInternalResponse getOrder(Long orderId) {
        ApiResponse<OrderInternalResponse> response = restClientBuilder.build()
                .get()
                .uri(orderServiceBaseUrl + "/api/orders/internal/{orderId}", orderId)
                .header("X-Internal-Service-Token", internalServiceToken)
                .retrieve()
                .body(new ParameterizedTypeReference<ApiResponse<OrderInternalResponse>>() {});

        if (response == null || response.data() == null) {
            throw new IllegalArgumentException("주문 정보를 가져오지 못했습니다.");
        }

        return response.data();
    }

    public void markOrderPaid(Long orderId) {
        restClientBuilder.build()
                .put()
                .uri(orderServiceBaseUrl + "/api/orders/{orderId}/paid", orderId)
                .header("X-Internal-Service-Token", internalServiceToken)
                .retrieve()
                .toBodilessEntity();
    }
}

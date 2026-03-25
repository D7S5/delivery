package com.delivery.order.client;

import com.delivery.common.ApiResponse;
import com.delivery.order.client.dto.StoreInternalResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "store-service", url = "${store-service.url}")
public interface StoreClient {

    @GetMapping("/internal/stores/{storeId}")
    ApiResponse<StoreInternalResponse> getInternalStore(@PathVariable("storeId") Long storeId);
}
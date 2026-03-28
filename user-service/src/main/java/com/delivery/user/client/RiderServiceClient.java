package com.delivery.user.client;

import com.delivery.common.ApiResponse;
import com.delivery.user.dto.CreateRiderClientRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "rider-service", url = "${rider-service.url}")
public interface RiderServiceClient {

    @PostMapping("/internal/riders")
    ApiResponse<Long> createRider(@RequestBody CreateRiderClientRequest request);
}
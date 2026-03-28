package com.example.riderservice.controller;

import com.delivery.common.ApiResponse;
import com.example.riderservice.dto.RiderAssignmentResponse;
import com.example.riderservice.service.RiderOrderQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/rider/orders")
@RequiredArgsConstructor
public class RiderOrderController {

    private final RiderOrderQueryService riderOrderQueryService;

    @GetMapping("/available")
    public ApiResponse<List<RiderAssignmentResponse>> getAvailableOrders(
            @RequestHeader("X-User-Id") Long riderUserId,
            @RequestHeader("X-User-Role") String role
    ) {
        return riderOrderQueryService.getAvailableOrders(riderUserId, role);
    }
}
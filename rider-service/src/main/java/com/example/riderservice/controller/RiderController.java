package com.example.riderservice.controller;

import com.delivery.common.ApiResponse;
import com.example.riderservice.dto.RiderLocationRequest;
import com.example.riderservice.dto.RiderStatusResponse;
import com.example.riderservice.service.RiderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/riders")
@RequiredArgsConstructor
public class RiderController {

    private final RiderService riderService;

    @GetMapping("/me/status")
    public ApiResponse<RiderStatusResponse> getMyStatus(
            @RequestHeader("X-User-Id") Long userId
    ) {
        return riderService.getMyStatus(userId);
    }

    @PutMapping("/me/online")
    public ApiResponse<Void> setOnline(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody RiderLocationRequest request
    ) {
        return riderService.setOnline(userId, request);
    }

    @PutMapping("/me/offline")
    public ApiResponse<Void> setOffline(
            @RequestHeader("X-User-Id") Long userId
    ) {
        return riderService.setOffline(userId);
    }
}
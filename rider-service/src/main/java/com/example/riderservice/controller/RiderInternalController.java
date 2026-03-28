package com.example.riderservice.controller;

import com.delivery.common.ApiResponse;
import com.example.riderservice.dto.CreateRiderRequest;
import com.example.riderservice.service.RiderRegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/riders")
@RequiredArgsConstructor
public class RiderInternalController {

    private final RiderRegistrationService riderRegistrationService;

    @PostMapping
    public ApiResponse<Long> createRider(@RequestBody CreateRiderRequest request) {
        return riderRegistrationService.createRider(request);
    }
}
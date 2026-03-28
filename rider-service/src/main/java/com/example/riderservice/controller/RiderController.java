package com.example.riderservice.controller;

import com.example.riderservice.dto.RiderLocationRequest;
import com.example.riderservice.service.RiderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rider")
@RequiredArgsConstructor
public class RiderController {

    private final RiderService riderService;

    @PatchMapping("/location")
    public String updateLocation(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody RiderLocationRequest request
    ) {
        riderService.updateLocation(userId, request);
        return "위치 업데이트 완료";
    }

    @PatchMapping("/online")
    public String online(@RequestHeader("X-User-Id") Long userId) {
        riderService.changeOnline(userId);
        return "온라인 전환 완료";
    }

    @PatchMapping("/offline")
    public String offline(@RequestHeader("X-User-Id") Long userId) {
        riderService.changeOffline(userId);
        return "오프라인 전환 완료";
    }
}


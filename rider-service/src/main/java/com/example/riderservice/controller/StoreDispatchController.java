package com.example.riderservice.controller;

import com.example.riderservice.dto.DispatchResponse;
import com.example.riderservice.service.DispatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rider/store/orders")
@RequiredArgsConstructor
public class StoreDispatchController {

    private final DispatchService dispatchService;

    @PatchMapping("/{orderReceiveId}/ready")
    public DispatchResponse readyForDelivery(@PathVariable Long orderReceiveId) {
        return dispatchService.markReadyAndDispatch(orderReceiveId);
    }
}
package com.delivery.store.controller;

import com.delivery.common.ApiResponse;
import com.delivery.store.dto.StoreInternalResponse;
import com.delivery.store.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/stores")
@RequiredArgsConstructor
public class StoreInternalController {

    private final StoreService service;

    @GetMapping("/{storeId}")
    public ApiResponse<StoreInternalResponse> getInternalStore(@PathVariable Long storeId) {
        return service.getInternalStore(storeId);
    }
}

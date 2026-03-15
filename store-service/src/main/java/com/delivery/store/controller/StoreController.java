package com.delivery.store.controller;

import com.delivery.common.ApiResponse;
import com.delivery.store.dto.*;
import com.delivery.store.service.StoreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    @PostMapping
    public ApiResponse<StoreResponse> createStore(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Email") String email,
            @RequestHeader("X-User-Role") String role,
            @Valid @RequestBody CreateStoreRequest request
            ) {
        return storeService.createStore(userId, email, role, request);
    }
    @GetMapping
    public ApiResponse<List<StoreListResponse>> getStores() {
        return storeService.getStores();
    }

    @PostMapping("/{storeId}")
    public ApiResponse<StoreDetailResponse> getStoreDetail(@PathVariable Long storeId) {
        return storeService.getStoreDetail(storeId);
    }

    @PostMapping("/{storeId}/menus")
    public ApiResponse<MenuResponse> createMenu(@PathVariable Long storeId,
                                                @RequestHeader("X-User-Id") Long userId,
                                                @RequestHeader("X-User-Role") String role,
                                                @Valid @RequestBody CreateMenuRequest request) {
        return storeService.createMenu(storeId, userId, role, request);
    }

    @GetMapping("/{storeId}/menus")
    public ApiResponse<List<MenuResponse>> getMenus(@PathVariable Long storeId) {
        return storeService.getMenus(storeId);
    }
}
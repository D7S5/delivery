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

    @GetMapping("/{storeId}")
    public ApiResponse<StoreDetailResponse> getStoreDetail(@PathVariable Long storeId) {
        return storeService.getStoreDetail(storeId);
    }

    @PutMapping("/{storeId}")
    public ApiResponse<StoreResponse> updateStore(
            @PathVariable Long storeId,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String role,
            @Valid @RequestBody UpdateStoreRequest request
    ) {
        return storeService.updateStore(storeId, userId, role, request);
    }

    @DeleteMapping("/{storeId}")
    public ApiResponse<Void> deleteStore(
            @PathVariable Long storeId,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String role
    ) {
        return storeService.deleteStore(storeId, userId, role);
    }

    @PostMapping("/{storeId}/menus")
    public ApiResponse<MenuResponse> createMenu(
            @PathVariable Long storeId,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String role,
            @Valid @RequestBody CreateMenuRequest request
    ) {
        return storeService.createMenu(storeId, userId, role, request);
    }

    @GetMapping("/{storeId}/menus")
    public ApiResponse<List<MenuResponse>> getMenus(@PathVariable Long storeId) {
        return storeService.getMenus(storeId);
    }

    @PutMapping("/{storeId}/menus/{menuId}")
    public ApiResponse<MenuResponse> updateMenu(
            @PathVariable Long storeId,
            @PathVariable Long menuId,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String role,
            @Valid @RequestBody UpdateMenuRequest request
    ) {
        return storeService.updateMenu(storeId, menuId, userId, role, request);
    }

    @DeleteMapping("/{storeId}/menus/{menuId}")
    public ApiResponse<Void> deleteMenu(
            @PathVariable Long storeId,
            @PathVariable Long menuId,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String role
    ) {
        return storeService.deleteMenu(storeId, menuId, userId, role);
    }
}
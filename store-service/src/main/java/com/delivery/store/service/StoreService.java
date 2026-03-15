package com.delivery.store.service;

import com.delivery.common.ApiResponse;
import com.delivery.store.dto.*;
import com.delivery.store.entity.Menu;
import com.delivery.store.entity.Store;
import com.delivery.store.entity.StoreStatus;
import com.delivery.store.repository.MenuRepository;
import com.delivery.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreService {

    private final StoreRepository storeRepository;
    private final MenuRepository menuRepository;

    @Transactional
    public ApiResponse<StoreResponse> createStore(Long ownerId, String ownerEmail, String role, CreateStoreRequest request) {
        validateOwner(role);

        Store store = Store.builder()
                .ownerId(ownerId)
                .ownerEmail(ownerEmail)
                .name(request.name())
                .address(request.address())
                .phoneNumber(request.phoneNumber())
                .minOrderAmount(request.minOrderAmount())
                .status(StoreStatus.OPEN)
                .createdAt(LocalDateTime.now())
                .build();

        Store savedStore = storeRepository.save(store);

        return new ApiResponse<>(true, StoreResponse.from(savedStore), "가게 등록이 완료되었습니다.");
    }

    public ApiResponse<List<StoreListResponse>> getStores() {
        List<StoreListResponse> stores = storeRepository.findAll()
                .stream()
                .map(StoreListResponse::from)
                .toList();

        return new ApiResponse<>(true, stores, "가게 목록 조회 성공");
    }

    public ApiResponse<StoreDetailResponse> getStoreDetail(Long storeId) {
        Store store = getStore(storeId);

        List<MenuResponse> menus = menuRepository.findByStoreId(storeId)
                .stream()
                .map(MenuResponse::from)
                .toList();

        StoreDetailResponse response = new StoreDetailResponse(
                StoreResponse.from(store),
                menus
        );

        return new ApiResponse<>(true, response, "가게 상세 조회 성공");
    }

    @Transactional
    public ApiResponse<MenuResponse> createMenu(Long storeId, Long ownerId, String role, CreateMenuRequest request) {
        validateOwner(role);

        Store store = getStore(storeId);

        if (!store.getOwnerId().equals(ownerId)) {
            throw new IllegalArgumentException("본인 가게에만 메뉴를 등록할 수 있습니다.");
        }

        Menu menu = Menu.builder()
                .storeId(storeId)
                .name(request.name())
                .price(request.price())
                .description(request.description())
                .soldOut(false)
                .createdAt(LocalDateTime.now())
                .build();

        Menu savedMenu = menuRepository.save(menu);

        return new ApiResponse<>(true, MenuResponse.from(savedMenu), "메뉴 등록이 완료되었습니다.");
    }

    public ApiResponse<List<MenuResponse>> getMenus(Long storeId) {
        getStore(storeId);

        List<MenuResponse> menus = menuRepository.findByStoreId(storeId)
                .stream()
                .map(MenuResponse::from)
                .toList();

        return new ApiResponse<>(true, menus, "메뉴 목록 조회 성공");
    }

    private Store getStore(Long storeId) {
        return storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("가게를 찾을 수 없습니다."));
    }

    private void validateOwner(String role) {
        if (!"OWNER".equals(role)) {
            throw new IllegalArgumentException("사장님 권한만 요청할 수 있습니다.");
        }
    }
}
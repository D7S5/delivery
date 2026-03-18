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

        List<MenuResponse> menus = menuRepository.findByStoreIdAndDeletedFalse(storeId) // soft delete
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
    public ApiResponse<StoreResponse> updateStore(Long storeId, Long ownerId, String role, UpdateStoreRequest request) {
        validateOwner(role);

        Store store = getStore(storeId);
        validateStoreOwner(store, ownerId);

        if (request.status() == StoreStatus.DELETE) {
            throw new IllegalArgumentException("삭제는 삭제 API를 사용하세요");
        }
        store.update(
                request.name(),
                request.address(),
                request.phoneNumber(),
                request.minOrderAmount(),
                request.status()
        );

        return new ApiResponse<>(true, StoreResponse.from(store), "가게 수정이 완료되었습니다.");
    }
    @Transactional
    public ApiResponse<Void> deleteStore(Long storeId, Long ownerId, String role) {
        validateOwner(role);

        Store store = getStore(storeId);
        validateStoreOwner(store, ownerId);

        store.delete();

        List<Menu> menus = menuRepository.findByStoreIdAndDeletedFalse(storeId);
        menus.forEach(Menu::delete);

        return new ApiResponse<>(true, null, "가게 삭제가 완료되었습니다.");
    }

    @Transactional
    public ApiResponse<Void> deleteMenu(Long storeId, Long menuId, Long ownerId, String role) {
        validateOwner(role);

        Store store = getStore(storeId);
        validateStoreOwner(store, ownerId);

        Menu menu = getMenu(menuId);

        if (!menu.getStoreId().equals(storeId)) {
            throw new IllegalArgumentException("해당 가게의 메뉴가 아닙니다.");
        }

        menu.delete();
        return new ApiResponse<>(true, null, "메뉴가 삭제되었습니다.");
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

        List<MenuResponse> menus = menuRepository.findByStoreIdAndDeletedFalse(storeId)
                .stream()
                .map(MenuResponse::from)
                .toList();

        return new ApiResponse<>(true, menus, "메뉴 목록 조회 성공");
    }

    @Transactional
    public ApiResponse<MenuResponse> updateMenu(Long storeId, Long menuId, Long ownerId, String role, UpdateMenuRequest request) {
        validateOwner(role);

        Store store = getStore(storeId);
        validateStoreOwner(store, ownerId);

        Menu menu = getMenu(menuId);

        if (!menu.getStoreId().equals(storeId)) {
            throw new IllegalArgumentException("해당 가게의 메뉴가 아닙니다.");
        }

        menu.update(
                request.name(),
                request.price(),
                request.description(),
                request.soldOut()
        );

        return new ApiResponse<>(true, MenuResponse.from(menu), "메뉴 수정이 완료되었습니다.");
    }

    private Menu getMenu(Long menuId) {
        return menuRepository.findByIdAndDeletedFalse(menuId)
                .orElseThrow(() -> new IllegalArgumentException("메뉴를 찾을 수 없습니다."));
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

    private void validateStoreOwner(Store store, Long ownerId) {
        if (!store.getOwnerId().equals(ownerId)) {
            throw new IllegalArgumentException("본인 가게만 수정 또는 삭제할 수 있습니다.");
        }
    }
}
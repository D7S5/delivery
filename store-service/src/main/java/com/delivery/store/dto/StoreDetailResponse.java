package com.delivery.store.dto;

import java.util.List;

public record StoreDetailResponse(
        StoreResponse store,
        List<MenuResponse> menus
) { }

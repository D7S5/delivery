package com.example.cartservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AddCartItemRequest {

    private Long storeId;
    private String storeName;
    private Long menuId;
    private String menuName;
    private Integer menuPrice;
    private Integer quantity;
    private Boolean replace;

}

package com.example.cartservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CartItemResponse {

    private Long cartItemId;
    private Long menuId;
    private String menuName;
    private Integer menuPrice;
    private Integer quantity;
    private Integer subTotal;

    public CartItemResponse(Long cartItemId, Long menuId, String menuName,
                            Integer menuPrice, Integer quantity) {
        this.cartItemId = cartItemId;
        this.menuId = menuId;
        this.menuName = menuName;
        this.menuPrice = menuPrice;
        this.quantity = quantity;
        this.subTotal = menuPrice * quantity;
    }
}
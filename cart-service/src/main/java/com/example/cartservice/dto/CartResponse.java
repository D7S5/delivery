package com.example.cartservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CartResponse {

    private Long cartId;
    private Long userId;
    private Long storeId;
    private String storeName;
    private List<CartItemResponse> items;
    private Integer totalPrice;

}
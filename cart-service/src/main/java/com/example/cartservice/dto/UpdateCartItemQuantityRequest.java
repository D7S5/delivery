package com.example.cartservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateCartItemQuantityRequest {
    private Integer quantity;

}

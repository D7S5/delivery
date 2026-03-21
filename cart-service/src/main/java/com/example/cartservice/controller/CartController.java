package com.example.cartservice.controller;

import com.example.cartservice.dto.AddCartItemRequest;
import com.example.cartservice.dto.CartResponse;
import com.example.cartservice.dto.UpdateCartItemQuantityRequest;
import com.example.cartservice.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public CartResponse getCart(@RequestHeader("X-USER-ID") Long userId) {
        return cartService.getCart(userId);
    }

    @PostMapping("/items")
    public CartResponse addItem(@RequestHeader("X-USER-ID") Long userId,
                                @RequestBody AddCartItemRequest request) {
        return cartService.addItem(userId, request);
    }

    @PatchMapping("/items/{cartItemId}")
    public CartResponse updateQuantity(@RequestHeader("X-USER-ID") Long userId,
                                       @PathVariable Long cartItemId,
                                       @RequestBody UpdateCartItemQuantityRequest request) {
        return cartService.updateQuantity(userId, cartItemId, request);
    }

    @DeleteMapping("/items/{cartItemId}")
    public CartResponse removeItem(@RequestHeader("X-USER-ID") Long userId,
                                   @PathVariable Long cartItemId) {
        return cartService.removeItem(userId, cartItemId);
    }

    @DeleteMapping
    public void clearCart(@RequestHeader("X-USER-ID") Long userId) {
        cartService.clearCart(userId);
    }
}

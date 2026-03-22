package com.example.cartservice.service;


import com.example.cartservice.dto.AddCartItemRequest;
import com.example.cartservice.dto.CartItemResponse;
import com.example.cartservice.dto.CartResponse;
import com.example.cartservice.dto.UpdateCartItemQuantityRequest;
import com.example.cartservice.entity.Cart;
import com.example.cartservice.entity.CartItem;
import com.example.cartservice.repository.CartRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;

    public CartResponse getCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> cartRepository.save(new Cart(userId)));

        return toResponse(cart);
    }

    public CartResponse addItem(Long userId, AddCartItemRequest request) {
        validateAddRequest(request);

        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> cartRepository.save(new Cart(userId)));

        if (!cart.isEmpty() && !cart.getStoreId().equals(request.getStoreId())) {
            if (Boolean.TRUE.equals(request.getReplace())) {
                cart.clear();
            } else { // 409
                throw new IllegalStateException("다른 가게 상품이 이미 장바구니에 있습니다.");
            }
        }

        if (cart.isEmpty()) {
            cart.changeStore(request.getStoreId(), request.getStoreName());
        }

        CartItem existingItem = cart.getItems().stream()
                .filter(item -> item.getMenuId().equals(request.getMenuId()))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            existingItem.increaseQuantity(request.getQuantity());
        } else {
            CartItem newItem = new CartItem(
                    request.getMenuId(),
                    request.getMenuName(),
                    request.getMenuPrice(),
                    request.getQuantity()
            );
            cart.addItem(newItem);
        }

        return toResponse(cart);
    }

    public CartResponse updateQuantity(Long userId, Long cartItemId, UpdateCartItemQuantityRequest request) {
        if (request.getQuantity() == null || request.getQuantity() < 1) {
            throw new IllegalArgumentException("수량은 1 이상이어야 합니다.");
        }

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("장바구니가 없습니다."));

        CartItem cartItem = cart.getItems().stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("장바구니 항목이 없습니다."));

        cartItem.changeQuantity(request.getQuantity());
        return toResponse(cart);
    }

    public CartResponse removeItem(Long userId, Long cartItemId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("장바구니가 없습니다."));

        CartItem cartItem = cart.getItems().stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("장바구니 항목이 없습니다."));

        cart.removeItem(cartItem);

        if (cart.isEmpty()) {
            cart.clear();
        }

        return toResponse(cart);
    }

    public void clearCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("장바구니가 없습니다."));

        cart.clear();
    }

    private void validateAddRequest(AddCartItemRequest request) {
        if (request.getStoreId() == null ||
                request.getStoreName() == null ||
                request.getMenuId() == null ||
                request.getMenuName() == null ||
                request.getMenuPrice() == null ||
                request.getQuantity() == null ||
                request.getQuantity() < 1) {
            throw new IllegalArgumentException("장바구니 요청값이 올바르지 않습니다.");
        }
    }

    private CartResponse toResponse(Cart cart) {
        List<CartItemResponse> itemResponses = cart.getItems().stream()
                .map(item -> new CartItemResponse(
                        item.getId(),
                        item.getMenuId(),
                        item.getMenuName(),
                        item.getMenuPrice(),
                        item.getQuantity()
                ))
                .toList();

        int totalPrice = itemResponses.stream()
                .mapToInt(CartItemResponse::getSubTotal)
                .sum();

        return new CartResponse(
                cart.getId(),
                cart.getUserId(),
                cart.getStoreId(),
                cart.getStoreName(),
                itemResponses,
                totalPrice
        );
    }
}

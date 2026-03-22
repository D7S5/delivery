package com.example.cartservice.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "carts")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "store_id")
    private Long storeId;

    @Column(name = "store_name")
    private String storeName;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();

    protected Cart() {
    }

    public Cart(Long userId) {
        this.userId = userId;
    }

    public void changeStore(Long storeId, String storeName) {
        this.storeId = storeId;
        this.storeName = storeName;
    }

    public void clear() {
        this.items.clear();;
        this.storeId = null;
        this.storeName = null;
    }

    public void addItem(CartItem item) {
        this.items.add(item);
        item.assignCart(this);
    }

    public void removeItem(CartItem item) {
        this.items.remove(item);
        item.assignCart(null);
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
}
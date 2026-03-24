package com.delivery.store.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class OrderReceiveItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long menuId;
    private String menuName;
    private Integer menuPrice;
    private Integer quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_receive_id")
    private OrderReceive orderReceive;

    @Builder
    public OrderReceiveItem(Long menuId, String menuName, Integer menuPrice, Integer quantity) {
        this.menuId = menuId;
        this.menuName = menuName;
        this.menuPrice = menuPrice;
        this.quantity = quantity;
    }

    public void assignOrderReceive(OrderReceive orderReceive) {
        this.orderReceive = orderReceive;
    }
}
package com.delivery.order.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "order_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long orderId;

    @Column(nullable = false)
    private Long menuId;

    @Column(nullable = false, length = 100)
    private String menuName;

    @Column(nullable = false)
    private Integer menuPrice;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Integer itemTotalPrice;

    @Builder
    public OrderItem(Long orderId, Long menuId, String menuName, Integer menuPrice,
                     Integer quantity, Integer itemTotalPrice) {
        this.orderId = orderId;
        this.menuId = menuId;
        this.menuName = menuName;
        this.menuPrice = menuPrice;
        this.quantity = quantity;
        this.itemTotalPrice = itemTotalPrice;
    }
}

package com.delivery.order.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long customerId;

    @Column(nullable = false, length = 30)
    private String customerEmail;

    @Column(nullable = false)
    private Long storeId;

    @Column(nullable = false, length = 30)
    private String storeName;

    @Column(nullable = false, length = 50)
    private String deliveryAddress;

    @Column(nullable = false)
    private Integer totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status;

    @Column(length = 500)
    private String requestMessage;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public Order(Long customerId, String customerEmail, Long storeId, String storeName,
                 String deliveryAddress, Integer totalAmount, OrderStatus status,
                 String requestMessage, LocalDateTime createdAt) {
        this.customerId = customerId;
        this.customerEmail = customerEmail;
        this.storeId = storeId;
        this.storeName = storeName;
        this.deliveryAddress = deliveryAddress;
        this.totalAmount = totalAmount;
        this.status = status;
        this.requestMessage = requestMessage;
        this.createdAt = createdAt;
    }

    public void changeStatus(OrderStatus status) {
        this.status = status;
    }
}
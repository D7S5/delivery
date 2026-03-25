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

    public void markPaid() {
        if (this.status == OrderStatus.CANCELED) {
            throw new IllegalArgumentException("취소된 주문은 결제할 수 없습니다.");
        }
        if (this.status == OrderStatus.PAID) {
            throw new IllegalArgumentException("이미 결제가 완료 된 주문입니다.");
        }
        this.status = OrderStatus.PAID;
    }

    public void cancel() {
        if (this.status == OrderStatus.PAID) {
            throw new IllegalArgumentException(("결제 완료 된 주문은 취소할 수 없습니다"));
        } // 가능하게
        if (this.status == OrderStatus.CANCELED) {
            throw new IllegalArgumentException("이미 취소된 주문입니다.");
        }
        this.status = OrderStatus.CANCELED;
    }

    public void prepared() {
        if (this.status != OrderStatus.PAID) {
            throw new IllegalArgumentException("준비중 상태로 변경하려면 결제가 완료되어야합니다.");
        }
        this.status = OrderStatus.PREPARING;
    }

    public void delivery() {
        if (this.status != OrderStatus.PREPARING) {
            throw new IllegalArgumentException("배달 상태로 변경하려면 준비중 상태여야 합니다.");
        }
        if (this.status == OrderStatus.CANCELED) {
            throw new IllegalArgumentException("이미 취소된 주문입니다.");
        }

        this.status = OrderStatus.DELIVERY;
    }

    public void complete() {
        if (this.status != OrderStatus.DELIVERY) {
            throw new IllegalArgumentException("배달 완료를 누르려면 배달 중이여야합니다.");
        }
        this.status = OrderStatus.COMPLETED;
    }
}
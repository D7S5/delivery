package com.delivery.store.entity;

import com.delivery.store.dto.StoreOrderItemDto;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
public class OrderReceive {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;

    private Long customerId;

    private String customerEmail;

    private Long storeId;

    private String storeName;

    private String deliveryAddress;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    private Integer totalAmount;

    private String requestMessage;

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "orderReceive", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderReceiveItem> items = new ArrayList<>();

    @Builder
    public OrderReceive(Long orderId, Long customerId,
                        String customerEmail, Long storeId,
                        String storeName, String deliveryAddress,
                        OrderStatus status,
                        Integer totalAmount, String requestMessage,
                        LocalDateTime createdAt) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.customerEmail = customerEmail;
        this.storeId = storeId;
        this.storeName = storeName;
        this.deliveryAddress = deliveryAddress;
        this.status = status;
        this.totalAmount = totalAmount;
        this.requestMessage = requestMessage;
        this.createdAt = createdAt;
    }

    public void addItem(OrderReceiveItem item) {
        items.add(item);
        item.assignOrderReceive(this);
    }

    public void startPreparing() {
        if (this.status != OrderStatus.RECEIVE_ORDER) {
            throw new IllegalArgumentException("접수된 주문만 준비 상태로 변경할 수 있습니다.");
        }
        this.status = OrderStatus.PREPARING;
    }

    public void startDelivery() {
        if (this.status != OrderStatus.PREPARING) {
            throw new IllegalArgumentException("준비 중 상태의 주문만 배달 상태로 변경할 수 있습니다.");
        }
        this.status = OrderStatus.DELIVERY;
    }

    public void complete() {
        if (this.status != OrderStatus.DELIVERY) {
            throw new IllegalArgumentException("배달 중인 주문만 완료 상태로 바꿀 수 있습니다.");
        }
        this.status = OrderStatus.COMPLETED;
    }

    public void cancel() {
        if (this.status == OrderStatus.COMPLETED) {
            throw new IllegalArgumentException("완료된 주문은 취소할 수 없습니다.");
        }
        this.status = OrderStatus.CANCELED;
    }
}

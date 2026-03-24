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
}

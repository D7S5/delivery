package com.example.riderservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "order_receive")
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

    private Integer totalAmount;
    private String requestMessage;

    // 가게 위치
    private Double storeLat;
    private Double storeLng;

    private Long riderId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    private LocalDateTime createdAt;

    @Version
    private Long version;

    public void markReadyForDelivery() {
        if (this.status != OrderStatus.PREPARING) {
            throw new IllegalStateException("준비중 상태에서만 배달 준비 완료로 변경할 수 있습니다.");
        }
        this.status = OrderStatus.READY_FOR_DELIVERY;
    }

    public void startDelivery(Long riderId) {
        if (this.status != OrderStatus.READY_FOR_DELIVERY) {
            throw new IllegalStateException("배달 준비 완료 상태에서만 배달 시작할 수 있습니다.");
        }
        this.riderId = riderId;
        this.status = OrderStatus.DELIVERY;
    }

    public void completeDelivery() {
        if (this.status != OrderStatus.DELIVERY) {
            throw new IllegalStateException("배달중 상태에서만 완료 처리할 수 있습니다.");
        }
        this.status = OrderStatus.COMPLETED;
    }

    public void cancel() {
        if (this.status == OrderStatus.COMPLETED) {
            throw new IllegalStateException("완료된 주문은 취소할 수 없습니다.");
        }
        this.status = OrderStatus.CANCELED;
    }
}

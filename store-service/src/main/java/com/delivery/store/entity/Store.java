package com.delivery.store.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Null;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "stores")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long ownerId;

    @Column(nullable = false, length = 30)
    private String ownerEmail;

    @Column(nullable = false, length = 30)
    private String name;

    @Column(nullable = false, length = 50)
    private String address;

    @Column(nullable = false, length = 30)
    private String phoneNumber;

    private Double storeLat;
    private Double storeLng;

    @Column(nullable = false)
    private Integer minOrderAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StoreStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public Store(Long ownerId, String ownerEmail, String name, String address,
                 String phoneNumber, Integer minOrderAmount,
                 Double storeLat, Double storeLng, StoreStatus status,
                 LocalDateTime createdAt) {
        this.ownerId = ownerId;
        this.ownerEmail = ownerEmail;
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.minOrderAmount = minOrderAmount;
        this.storeLat = storeLat;
        this.storeLng = storeLng;
        this.status = status;
        this.createdAt = createdAt;
    }

    public void update(String name, String address, String phoneNumber, Double storeLat, Double storeLng, Integer minOrderAmount, StoreStatus status){
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.storeLat = storeLat;
        this.storeLng = storeLng;
        this.minOrderAmount = minOrderAmount;
        this.status = status;
    }

    public void delete() {
        this.status = StoreStatus.DELETE;
    }

}

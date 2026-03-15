package com.delivery.store.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "menus")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long storeId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false, length = 255)
    private String description;

    @Column(nullable = false)
    private Boolean soldOut;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public Menu(Long storeId, String name, Integer price, String description,
                Boolean soldOut, LocalDateTime createdAt) {
        this.storeId = storeId;
        this.name = name;
        this.price = price;
        this.description = description;
        this.soldOut = soldOut;
        this.createdAt = createdAt;
    }
}

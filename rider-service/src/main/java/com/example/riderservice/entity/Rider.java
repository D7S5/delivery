package com.example.riderservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Rider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String riderName;

    @Enumerated(EnumType.STRING)
    private RiderStatus status;

    private Double currentLat;
    private Double currentLng;

    private LocalDateTime lastLocationUpdatedAt;

    public Rider(Long userId, String riderName, RiderStatus status) {
        this.userId = userId;
        this.riderName = riderName;
        this.status = status;
    }

    public void updateLocation(Double lat, Double lng) {
        this.currentLat = lat;
        this.currentLng = lng;
        this.lastLocationUpdatedAt = LocalDateTime.now();
    }

    public void changeStatus(RiderStatus status) {
        this.status = status;
    }
}
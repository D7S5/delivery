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

    public void setOnline(Double latitude, Double longitude) {
        if (this.status == RiderStatus.DELIVERING) {
            throw new IllegalStateException("배달 중에는 온라인 상태를 다시 변경할 수 없습니다.");
        }
        this.status = RiderStatus.ONLINE;
        updateLocation(latitude, longitude);
    }

    public void setOffline() {
        if (this.status == RiderStatus.DELIVERING) {
            throw new IllegalStateException("배달 중에는 오프라인으로 전환할 수 없습니다.");
        }
        this.status = RiderStatus.OFFLINE;
    }

    public void changeStatus(RiderStatus status) {
        this.status = status;
    }
}
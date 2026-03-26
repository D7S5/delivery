package com.example.riderservice.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "delivery_assignment")
public class DeliveryAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long orderReceiveId;

    @Column(nullable = false)
    private Long riderId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssignmentStatus status;

    private LocalDateTime assignedAt;
    private LocalDateTime respondedAt;
    private LocalDateTime expiresAt;

    @Version
    private Long version;

    @Builder
    public DeliveryAssignment(Long orderReceiveId, Long riderId, AssignmentStatus status,
                              LocalDateTime assignedAt, LocalDateTime expiresAt) {
        this.orderReceiveId = orderReceiveId;
        this.riderId = riderId;
        this.status = status;
        this.assignedAt = assignedAt;
        this.expiresAt = expiresAt;
    }

    public void accept() {
        if (this.status != AssignmentStatus.ASSIGNED) {
            throw new IllegalStateException("수락 가능한 배차가 아닙니다.");
        }
        this.status = AssignmentStatus.ACCEPTED;
        this.respondedAt = LocalDateTime.now();
    }

    public void reject() {
        if (this.status != AssignmentStatus.ASSIGNED) {
            throw new IllegalStateException("거절 가능한 배차가 아닙니다.");
        }
        this.status = AssignmentStatus.REJECTED;
        this.respondedAt = LocalDateTime.now();
    }

    public void expire() {
        if (this.status != AssignmentStatus.ASSIGNED) {
            return;
        }
        this.status = AssignmentStatus.EXPIRED;
        this.respondedAt = LocalDateTime.now();
    }
}